#!/usr/bin/python

# Copyright (C) 2013 Gerwin Sturm, FoldedSoft e.U.
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#      http://www.apache.org/licenses/LICENSE-2.0#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.

"""Methods for Check in service"""

import json
import logging
import urllib2
import webapp2
import util
import foursquare

from apiclient.errors import HttpError

__all__ = ["handle_item", "handle_location", "WELCOMES", "ROUTES"]

_BUNDLE_ID = "glasquare_welcome"

class PlaceHandler(webapp2.RequestHandler):
    """Handler to create dummy pages from Google Places API result,
    so those pages can be added as App Activities"""

    @util.auth_required
    def get(self):
        logging.info('Inserting timeline item')
        client = foursquare.Foursquare(client_id='3XRAA220QQWY4XHJH11TGRGEFYSW03YOBUL3225Y3KBMJ3XY', client_secret='4WYHXNQQVUYSTJGFMQZNRFBUKU4GFKPEBKFM0HFBVD42HN5U')
        resp = client.venues.explore(params={'ll': '50.051642,14.407407', 'limit':'10', 'radius':1000})
        map = "glass://map?w=640&h=360&"
        #map += "marker=0;%s,%s" % (location["latitude"], location["longitude"])
        i = 0
        for place in resp['groups'][0]['items']:
            venue = place['venue']
            name = venue['name'];
            category = venue['categories'][0]['name']
            lat = venue['location']['lat']
            lon = venue['location']['lng']

            map += "&marker=%s;%s,%s" % (i, lat, lon)
            logging.info(name+' ('+category+'): '+str(lat)+','+str(lon))
            i = i + 1
            if i > 10:
                break

        count = i - 1
        html = "<article class=\"photo\">"
        html += "<img src=\"%s\" width=\"100%%\" height=\"100%%\">" % map
        html += "<div class=\"photo-overlay\"></div>"
        html += "<footer><div>%s place%s nearby</div></footer>" % (count, "" if count == 1 else "s")
        html += "</article>"

        body = {
            'notification': {'level': 'DEFAULT'},
            'html': html
        }

        # self.mirror_service is initialized in util.auth_required.
        self.mirror_service.timeline().insert(body=body).execute()
        self.response.out.write("Timeline item has been inserted")
        return "Test"

ROUTES = [(r"/welcome", PlaceHandler)]