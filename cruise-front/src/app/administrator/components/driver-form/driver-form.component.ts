import { Component } from '@angular/core';
import { FormBuilder, Validators } from '@angular/forms';
import { DriverCreate } from 'src/app/driver/models/driver_create-dto';
import { Driver } from 'src/app/driver/models/driver-dto';
import { Vehicle } from 'src/app/driver/models/vehicle-dto';
import { Router } from '@angular/router';
import { License } from 'src/app/driver/models/license-dto';
import { CreateDriverService } from '../../services/create-driver.service';
import { CurrentLocation } from 'src/app/driver/models/current_location-dto';
import { RegisteredUserDTO } from 'src/app/user/models/registered-user-dto';

@Component({
  selector: 'app-driver-form',
  templateUrl: './driver-form.component.html',
  styleUrls: ['./driver-form.component.css'],
})
export class DriverFormComponent {
  hide: boolean = true;
  driver: RegisteredUserDTO = <RegisteredUserDTO>{};
  profilePicture: any = '';
  driversLicense: License = <License>{
    name: 'Drivers license',
    documentImage: '',
  };
  trafficLicense: License = <License>{
    name: 'Traffic license',
    documentImage: '',
  };

  constructor(
    private formBuilder: FormBuilder,
    private router: Router,
    private createDriverService: CreateDriverService
  ) {}

  driverForm = this.formBuilder.group({
    profil_picture: [''],
    email: ['', Validators.required],
    name: ['', Validators.required],
    surname: ['', Validators.required],
    phone: ['', Validators.required],
    address: ['', Validators.required],
    password: ['', Validators.required],
  });

  vehicleForm = this.formBuilder.group({
    model_name: ['', Validators.required],
    registration_num: ['', Validators.required],
    num_seats: ['', Validators.required],
    pets: [''],
    babys: [''],
    vehicle_types: ['', Validators.required],
  });

  checkIfFileValid(event: any): boolean {
    if (event.target.files[0] && event.target.files[0].name.length > 0) {
      const fileType = event.target.files[0].type;

      if (fileType.match('image/*') != null) {
        return true;
      }
      return false;
    }
    return false;
  }

  selectProfilePicture(event: any) {
    if (this.checkIfFileValid(event)) {
      const reader = new FileReader();
      reader.readAsDataURL(event.target.files[0]);
      reader.onload = (_event: any) => {
        this.profilePicture = reader.result;
      };
    } else {
      this.profilePicture = '';
    }
  }

  selectDriverLicense(event: any) {
    if (this.checkIfFileValid(event)) {
      const reader = new FileReader();
      reader.readAsDataURL(event.target.files[0]);

      reader.onload = (_event: any) => {
        this.driversLicense = <License>{
          name: 'Drivers license',
          documentImage: reader.result,
        };
      };
    } else {
      this.driversLicense = <License>{
        name: 'Drivers license',
        documentImage: '',
      };
    }
  }

  selectTrafficLicense(event: any) {
    if (this.checkIfFileValid(event)) {
      const reader = new FileReader();
      reader.readAsDataURL(event.target.files[0]);

      reader.onload = (_event: any) => {
        this.trafficLicense = <License>{
          name: 'Traffic license',
          documentImage: reader.result,
        };
      };
    } else {
      this.trafficLicense = this.trafficLicense = <License>{
        name: 'Traffic license',
        documentImage: '',
      };
    }
  }

  getVehicleData(): Vehicle {
    const location = <CurrentLocation>{
      address: 'Bulevar oslobodjenja 46',
      latitude: 45.267136,
      longitude: 19.833549,
    };

    return <Vehicle>{
      vehicleType: this.vehicleForm.value.vehicle_types,
      model: this.vehicleForm.value.model_name,
      licenseNumber: this.vehicleForm.value.registration_num,
      currentLocation: location,
      passengerSeats: Number(this.vehicleForm.value.num_seats),
      babyTransport: this.vehicleForm.value.babys == 'true' ? true : false,
      petTransport: this.vehicleForm.value.pets == 'true' ? true : false,
    };
  }

  getDriverData(): DriverCreate {
    return <DriverCreate>{
      name: this.driverForm.value.name,
      surname: this.driverForm.value.surname,
      telephoneNumber: this.driverForm.value.phone,
      email: this.driverForm.value.email,
      address: this.driverForm.value.address,
      password: this.driverForm.value.password,
      profilePicture: this.profilePicture,
    };
  }

  createDriver() {
    if (this.driverForm.valid && this.vehicleForm.valid) {
      const vehicle: Vehicle = this.getVehicleData();
      const newDriver: DriverCreate = this.getDriverData();

      this.createDriverService
        .createDriver(newDriver)
        .subscribe((response: RegisteredUserDTO) => {
          this.driver = response;

          this.createDriverService
            .createVehicle(vehicle, this.driver.id)
            .subscribe((_: Vehicle) => {});
          this.createDriverService
            .createDocument(this.driversLicense, this.driver.id)
            .subscribe((_: License) => {});
          this.createDriverService
            .createDocument(this.trafficLicense, this.driver.id)
            .subscribe((_: License) => {});

          this.routBack();
        });
    }
  }
  routBack(): void {
    this.router.navigateByUrl('/passenger-main');
  }
}
