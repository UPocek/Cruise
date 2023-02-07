from fastapi import FastAPI
from pydantic import BaseModel
import requests
import threading
import json
import random
import math

GOOGLE_MAPS_API_KEY = 'AIzaSyD6xOzj2hrFrXPkVEDSnNsfmHd1Tup-0gU'
URL_BASE = 'http://localhost:8080'
UPDATE_INTERVAL_IN_SECONDS = 5
MAX_RIDE_DISTANCE = 0.025
BOUNDS = 0.025
CAR_SPEED_IN_KM_H = 60
ONE_KM_H_TO_POINTS = 0.0004
CAR_SPEED_TO_USE = ONE_KM_H_TO_POINTS * CAR_SPEED_IN_KM_H / 60
all_vehicles = []
active_rides = {}

class Location(BaseModel):
    address: str
    latitude: float
    longitude: float
    
class Vehicle(BaseModel):
    id: int
    driverId: int
    currentLocation: Location
    status: str
    
class RideRequest(BaseModel):
    rideId: int
    vehicle: Vehicle
    location: Location

app = FastAPI()

@app.post("/ride-request", status_code=200)
def drive_vehicle_to_passenger(ride_request: RideRequest):
    vehicle_for_ride:Vehicle = ride_request.vehicle
    destination_location:Location = ride_request.location
    for vehicle_on_map in all_vehicles:
        if vehicle_on_map['vehicle']['id'] == vehicle_for_ride.id:
            vehicle_to_steer = vehicle_on_map
    route = {'departure':{'latitude':vehicle_for_ride.currentLocation.latitude, 'longitude':vehicle_for_ride.currentLocation.longitude}, 'destination':{'latitude':destination_location.latitude, 'longitude':destination_location.longitude}}
    active_rides[vehicle_for_ride.id] = ride_request.rideId
    vehicle_to_steer['vehicle']['status'] = vehicle_for_ride.status
    assign_new_route(vehicle_to_steer, route)
    set_drive_navigation(vehicle_to_steer)
    print("New ride request")

@app.post("/add-vehicle", status_code=200)
def add_vehicle(vehicle: Vehicle):
    v = {'id':vehicle.id, 'driverId':vehicle.driverId, 'currentLocation': {'address': vehicle.currentLocation.address, 'latitude':vehicle.currentLocation.latitude, 'longitude': vehicle.currentLocation.longitude}, 'status': vehicle.status}
    all_vehicles.append({'vehicle':v, 'departure': {'latitude':v['currentLocation']['latitude'], 'longitude':v['currentLocation']['longitude']}, 'destination': {'latitude':v['currentLocation']['latitude'], 'longitude':v['currentLocation']['longitude']}, 'bounds': calculate_bounds(v)})
    print("New vehicle added")

@app.get("/remove-vehicle/{vehicle_id}", status_code=200)
def remove_vehicle(vehicle_id:int):
    for vehicle in all_vehicles:
        if vehicle['vehicle']['id'] == vehicle_id:
            all_vehicles.remove(vehicle)
            print("Vehicle removed")
            break

def get_all_vehicles():
    response = requests.get(f'{URL_BASE}/api/driver/all-active-vehicles')
    data = response.json()
    for vehicle in data:
        all_vehicles.append({'vehicle':vehicle, 'departure': {'latitude':vehicle['currentLocation']['latitude'], 'longitude':vehicle['currentLocation']['longitude']}, 'destination': {'latitude':vehicle['currentLocation']['latitude'], 'longitude':vehicle['currentLocation']['longitude']}, 'bounds': calculate_bounds(vehicle)})
    print(all_vehicles)

def calculate_bounds(vehicle):
    current_latitude, current_longitude = vehicle['currentLocation']['latitude'], vehicle['currentLocation']['longitude']
    bounds = {'min-latitude':current_latitude - BOUNDS, 'min-longitude':current_longitude - BOUNDS, 'max-latitude':current_latitude + BOUNDS, 'max-longitude':current_longitude + BOUNDS}
    return bounds

def monitor_progress():
    for vehicle in all_vehicles:
        try:
            if(car_arrived(vehicle['vehicle']['currentLocation'], vehicle['destination'])):
                if vehicle['vehicle']['status'] == 'FREE':
                    assign_new_route(vehicle)
                    set_drive_navigation(vehicle)
                elif vehicle['vehicle']['status'] == 'ACCEPTED':
                    notify_driver_has_arrived_to_pick_up_location(active_rides.pop(vehicle['vehicle']['id']))
                elif vehicle['vehicle']['status'] == 'ACTIVE':
                    notify_driver_finished_ride(active_rides.pop(vehicle['vehicle']['id']))
                    vehicle['vehicle']['status'] = 'FREE'
                else:
                    assign_new_route(vehicle)
                    set_drive_navigation(vehicle)
            elif(car_arrived(vehicle['vehicle']['currentLocation'],{'latitude':vehicle['route']['routes'][0]['legs'][0]['steps'][vehicle['progress']]['end_location']['lat'], 'longitude':vehicle['route']['routes'][0]['legs'][0]['steps'][vehicle['progress']]['end_location']['lng']})):
                vehicle['progress'] += 1
                set_drive_navigation(vehicle)
        except Exception as e:
            print(e)
            vehicle['vehicle']['currentLocation'] = {'address': 'Bulevar Oslobodjenja 40', 'latitude':45.258617, 'longitude': 19.832926}
            assign_new_route(vehicle)
            set_drive_navigation(vehicle)
           

def car_arrived(current_location, destination_location):
    return (abs(current_location['latitude'] - destination_location['latitude']) < CAR_SPEED_TO_USE) and (abs(current_location['longitude'] - destination_location['longitude']) < CAR_SPEED_TO_USE)
        

def assign_new_route(vehicle, route=None):
    if route == None:
        vehicle['departure'], vehicle['destination'] = generate_random_route(vehicle)
    else:
        vehicle['departure'], vehicle['destination'] = route['departure'], route['destination']
    vehicle['route'] = get_route_steps(vehicle['departure'], vehicle['destination'])
    vehicle['progress'] = 0

def generate_random_route(vehicle):
    direction = random.choice([-1,1])
    
    current_latitude, current_longitude = vehicle['vehicle']['currentLocation']['latitude'], vehicle['vehicle']['currentLocation']['longitude']
    departure = {'latitude':current_latitude, 'longitude':current_longitude}
    lat_destination = current_latitude + random.random() * MAX_RIDE_DISTANCE * direction
    lat_destination = min(vehicle['bounds']['max-latitude'], lat_destination)
    lat_destination = max(vehicle['bounds']['min-latitude'], lat_destination)
    lng_destination = current_longitude + random.random() * MAX_RIDE_DISTANCE * direction
    lng_destination = min(vehicle['bounds']['max-longitude'], lng_destination)
    lng_destination = max(vehicle['bounds']['min-longitude'], lng_destination)
    destination = {'latitude':lat_destination, 'longitude':lng_destination}
    return departure, destination

def get_route_steps(departure, destination):
    url = f'https://maps.googleapis.com/maps/api/directions/json?origin={departure["latitude"]},{departure["longitude"]}&destination={destination["latitude"]},{destination["longitude"]}&key={GOOGLE_MAPS_API_KEY}'
    payload={}
    headers = {}
    response = requests.request("GET", url, headers=headers, data=payload)
    json_response = json.loads(response.text)
    return json_response

def set_drive_navigation(vehicle):
    start_position = (vehicle['vehicle']['currentLocation']['latitude'], vehicle['vehicle']['currentLocation']['longitude'])
    next_stop_position = (vehicle['route']['routes'][0]['legs'][0]['steps'][vehicle['progress']]['end_location']['lat'], vehicle['route']['routes'][0]['legs'][0]['steps'][vehicle['progress']]['end_location']['lng'])
    vehicle['navigation'] = calculate_direction_vector(start_position, next_stop_position)

def calculate_direction_vector(start_point, end_point):
    direction_vector = (end_point[0]-start_point[0], end_point[1]-start_point[1])
    vector_magnitude = math.sqrt(direction_vector[0]**2 + direction_vector[1]**2)
    directioned_unit_vector = {'latitude':direction_vector[0]/vector_magnitude, 'longitude':direction_vector[1]/vector_magnitude}
    return directioned_unit_vector

def change_position():
    for vehicle in all_vehicles:
        vehicle['vehicle']['currentLocation']['latitude'] += vehicle['navigation']['latitude'] * CAR_SPEED_TO_USE
        vehicle['vehicle']['currentLocation']['longitude'] += vehicle['navigation']['longitude'] * CAR_SPEED_TO_USE
      
def drive():
    t = threading.Timer(1, drive)
    t.setDaemon(True)
    t.start()
    monitor_progress()
    change_position()
    
def update_vehicle_locations():
    t = threading.Timer(UPDATE_INTERVAL_IN_SECONDS, update_vehicle_locations)
    t.setDaemon(True)
    t.start()
    current_vehicles_state = [vehicle['vehicle'] for vehicle in all_vehicles]
    response = requests.put(url = f'{URL_BASE}/api/vehicle/all/location', data = json.dumps({'totalCount':len(current_vehicles_state), 'results': current_vehicles_state}), headers={'Content-Type':'application/json'})
    if(response.status_code != 200):
        print('update_vehicle_locations',response.status_code, response.reason)

def notify_driver_has_arrived_to_pick_up_location(rideId):
    response = requests.put(url = f'{URL_BASE}/api/ride/driver-on-address/{rideId}')
    if(response.status_code != 200):
        print('notify_driver_has_arrived_to_pick_up_location',response.status_code, response.reason)

def notify_driver_finished_ride(rideId):
    response = requests.put(url = f'{URL_BASE}/api/ride/{rideId}/end')
    if(response.status_code != 200):
        print('notify_driver_finished_ride',response.status_code, response.reason)
    

def main():
    get_all_vehicles()
    monitor_progress()
    update_vehicle_locations()
    drive()


main()