# cruise-rides
Python script for manupulating vehicles for Uber clone app

### Run the script
Use this command to run the script:
> uvicorn main:app --reload

### Features:

1. Script will drive around all 'FREE' vehicles
1. After ride was requested selected vehicle will drive towards pickup location
1. When vehicle arrives at departure location it will be redirected to destination location and it will drive itself towards it
1. After vehicle arrives at destination location will will switch to status 'FREE' and start circle again
