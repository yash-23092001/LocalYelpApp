
The idea of the app is to popularize local specialties that are not documented on google maps / yelp. For things like local events, the best transportation systems, best food etc. 

Main features are account based reviews, ability to query the type of thing i.e food/transportation/event,local area browsing (based on your long/lat) and ability to post events/areas. 

Things to work on:

Implement Nearby using Geocode coordinates and Location Coordinates
                                                
Review with same name and place : Check longitude and latitude and name, if location already exists, then add on to collection under username. USE .add() instead of .set() to create random ID. This allows multiple same name places. If have time implement this. 
                                                
Upload pictures : Create intent with storage/camera and then find out how to upload high quality picture to Firestore. Right now only URL pictures can be loaded. Also for location page, use a vector/array of images so you can swipe through. Right now only one image per place.

Improve performance using Async task for database queries and stuff. Right now small dataset so unnecessary. 

Emulator used: 720x1280 Resolution,API 19 Android 4.4 Lollipop
