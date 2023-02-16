# cruise-rides
Python script for manupulating vehicles for Uber clone app

### Run the script
Use this command to run the script:
> uvicorn main:app --reload

### Features:

1. Script will drive around all 'FREE' vehicles
1. After ride was requested selected vehicle will change status to 'INRIDE' and drive towards pickup location
1. When vehicle arrives at departure location it will be redirected to destination location and it will drive itself towards it
1. After vehicle arrives at destination location it will switch to status 'FREE' and start cycle again

### Notes:

1. Packages `uvloop` and `watchfiles` are not required for running server on windoows
