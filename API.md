# Finch Template API

# Overview

## Conventions

This is the FInch Template API. Conventions:

* All calls run over TLS.
* The API accepts & returns JSON, all `POST` & `PUT` payloads are specified in JSON (i.e. not `application/x-www-form-urlencoded`).
* All calls accept (for `PUT` & `POST`) & return JSON objects, e.g. `{"data":{"foo":"bar"}}`
* All parameters are specified in a `data` object at the root level.
* All responses return JSON objects, which will *one of* (but not both):
  * `error` - if an error ocurred.
  * `data` - if the response was a success.
* Additional parameters may be specified in the query string where documented.
* All time stamps are [ISO 8601](http://www.w3.org/TR/NOTE-datetime) strings.
* Appropriate status codes are used, not limited to:
  * `200` - OK
  * `201` - New resource created
  * `401` - Unauthorized
  * `404` - Not found

## Required Parameters

All requests require authentication using [Hawk](https://github.com/hueniverse/hawk), which is a HMAC style
authentication method. Provide an `Authorization` header containing the authorisation details.

```
GET /resource/1?b=1&a=2 HTTP/1.1
Host: example.com:8000
Authorization: Hawk id="dh37fgj492je", ts="1353832234", nonce="j4h3g2", ext="some-app-ext-data", mac="6R4rV5iE+NPoym+WwjeHzjAGXUtLNIxmo1vpMofpLAE="
```

If you don't include correct authentication you will get a `401` with a `WWW-Authenticate: Hawk` header:

```
$ curl -i http://liege-api/v1/users/sign-out -d '{ "data": {} }'
HTTP/1.1 401 Unauthorized
Content-Type: application/json;charset=utf-8
Content-Length: 143

{"error":{"message":"Missing auth token; include header 'X-Liege-Auth-Token' or Cookie 'liege_session_id'","type":"AuthenticationFailedError"}}
```


All `POST` API operations expect the following params in their JSON payload (at the root level):

* `data`: Call specific data, as per below.

## Errors

All errors from the API return a top level `error` object, which contains information about the error. This will include at least the following (but possibly more) fields:

* `message` - A human readable overview of the error (the exception message).
* `type` - The type of the exception.
* `cause` - (optional) If the exception has an underlying cause, its message.

For example, a missing request body:

    $ curl -i -d '' http://liege-api/v1/users/sign-in
    HTTP/1.1 400 Bad Request
    Content-Type: application/json;charset=utf-8
    Content-Length: 85

    {"error":{"message":"Required body not present in the request.","type":"NotPresent"}}

And a missing `context` field in the request body:

    $ curl -i http://liege-api/v1/users/sign-in -d '{"data":{"app_id":"123","device_id":"123","build_version":"1","fb_token":"abc"}}'
    HTTP/1.1 400 Bad Request
    Content-Type: application/json;charset=utf-8
    Content-Length: 186

    {"error":{"message":"body cannot be converted to SignInRequest: expected json value got & (line 1, column 1).","type":"NotParsed","cause":"expected json value got & (line 1, column 1)"}}

## Environments

### Production

* `http://liege-api.com`

Substitute the domain `liege-api` in the example below with this.

# Operations

## Sign In

Signs a user into the system via a Facebook session token. This assumes that you have signed in with Facebook first & have a valid token (it will be checked with Facebook).

Once authenticated, you will get back an auth token to be used with all subsequent requests:

* An `auth_token` in the response;
* A `Set-Cookie` HTTP header with the same token;
* An `X-Liege-Auth-Token` HTTP header with the same token.

You must provide the authentication token with all subsequent requests either as a HTTP `Cookie` or a `X-Liege-Auth-Token` header. Re-authenticating with the same Facebook token will yield the same authentication token.

Note that successive calls to `sign-in` *may not* return the same auth token. You should use the latest token returned rather than relying on a token from a previous sign in attempt.

* **URL pattern:** `http://liege-api/v1/users/sign-in`

* **HTTP Verb:** `POST`

* **Parameters:**
  * `context`
    * `app_id` - The ID of the app; the bundle ID on iOS & the package name on Android.
    * `device_id` - The ID of the device. On iOS this is the UDID (or equivalent).
    * `build_version` - The version of the app making the API request, e.g. "1.0.4 build 100".
  * `data`
    * `fb_token` - The Facebook authentication token, received from a Facebook SDK after authenticating against Facebook.

* **Response:**
  * `data`
    * `auth_token` - The authentication token you must provide when making all other requests to the API.
    * `fb_token` - A Facebook authentication token, which may be a different one to the one passed (e.g. if the old one has expired).
    * `name` - The person's (Facebook) name.
    * `email` - The person's (Facebook) email address.
    * `location` - The person's (Facebook) location, if the user has allowed access, otherwise this field is not present.
    * `avatar_url` - The person's (Facebook) avatar URL.

### Example Request/Response

#### Success

    $ curl -i -X POST http://liege-api/v1/users/sign-in --data '{ "context": {"app_id": "com.liege.ios", "device_id": "dddd0354bd884f929b66e1b2daf4f0e89184", "build_version": "1.0.0"}, "data": { "fb_token": "CAAXc7fOP...29ywDkfR" } }'
    HTTP/1.1 200 OK
    X-Liege-Auth-Token: 64d8c374-3e5e-4b7e-8f07-f50e29fbffb0
    Set-Cookie: liege_session_id=64d8c374-3e5e-4b7e-8f07-f50e29fbffb0; Expires=Thu, 13 Nov 2025 05:18:46 GMT; Path=/; HTTPOnly
    Content-Type: application/json;charset=utf-8
    Content-Length: 627

    {"data":{"name":"Tom Adams","location":"Melbourne, Australia","email":"tomjadams@gmail.com","fb_token":"CAAXc7fOP...29ywDkfR","auth_token":"64d8c374-3e5e-4b7e-8f07-f50e29fbffb0","avatar_url":"https://fbcdn-profile-a.akamaihd.net/hprofile-ak-xtp1/v/t1.0-1/c149.2.427.427/s50x50/10653687_10152747268141660_2514048174912716451_n.jpg?oh=097b2d771a7b64fb95fa135c630b013b&oe=56E8C045&__gda__=1458891858_10b1d11595c9cb0a9fa4d8664cd66e4c"}}

#### Failure

    $ curl -i http://localhost:8080/v1/users/sign-in -d '{ "context": {"app_id": "com.liege.ios", "device_id": "dddd0354bd884f929b66e1b2daf4f0e89184", "build_version": "1.0.0"}, "data": { "fb_token": "CAAXc7fOP...29ywDkfR" } }'
    HTTP/1.1 401 Unauthorized
    Content-Type: application/json;charset=utf-8
    Content-Length: 476

    {"error":{"message":"Facebook authentication failed using token 'CAAXc7fOP...29ywDkfR'","type":"AuthenticationFailedError","cause":"Error validating access token: Session has expired on Wednesday, 18-Nov-15 00:00:00 PST. The current time is Wednesday, 18-Nov-15 02:29:56 PST."}}

## Sign Out

Signs the current user out of the system. Note that this operation will *never* return a `4xx` status if the token isn't valid, you will always get a `200`.

* **URL pattern:** `http://liege-api/v1/users/sign-out`

* **HTTP Verb:** `POST`

* **Parameters:**
  * Specified in headers.

* **Response:**
  * An empty JSON payload.

### Example Request/Response

    $ curl -i -X POST -H "X-Liege-Auth-Token: 64d8c374-3e5e-4b7e-8f07-f50e29fbffb0" http://liege-api/v1/users/sign-out -d '{ "data": {} }'
    HTTP/1.1 200 OK
    Set-Cookie: liege_session_id=; Expires=Fri, 18 Nov 2005 05:17:37 GMT; Path=/; HTTPOnly
    Content-Type: application/json;charset=utf-8
    Content-Length: 11

    {"data":{}}

## Ride List

A summary of all rides available for the currently signed in user.

* **HTTP Verb:** `GET`

* **Parameters:**
  * No additional parameters.

* **Response:**
  * `data`
    * `rides` - A list of rides.
      * name - The name of the ride.
      * `start_time` - The time that the ride starts.
      * `location` - The location of the start of the ride.
        * `name` - The name of the start location.
        * `backend_id` - The identifier of the location in the backend system.
        * `backend_type` - The "source" of the location in the backend system, which will be "facebook" or "strava".
      * `group` - (optional) The group this ride belongs to.
        * `name` - The name of the group.
        * `backend_id` - The identifier of the group in the backend system.
        * `backend_type` - The "source" of the group in the backend system, which will be "facebook" or "strava".
      * `picture_url` - (optional) A URL to a picture for this ride, likely a "cover image" for Facebook based events.
      * `backend_type` - The "source" of the event in the backend system, which will be "facebook" or "strava".
      * `backend_id` - The identifier of the ride in the backend system.

* **URL pattern:** `http://liege-api/v1/rides`

### Example Request/Response

    $ curl -i -X GET -H "X-Liege-Auth-Token: a3a174fd-525b-45c1-b78e-0184c0ef651b" http://liege-api/v1/rides
    HTTP/1.1 200 OK
    Content-Type: application/json;charset=utf-8
    Content-Length: 542

    {"data":{"rides":[{"name":"Tour de Eltham","start_time":"2015-12-21T09:46:11.623Z","location":{"name":"Peak Heidelberg"},"backend_type":"facebook","backend_id":"001","attendance":"in","picture_url":"http://theclimbingcyclist.com/wp-content/uploads/2010/07/Eltham-loop-ride-11.07.10-006.jpg","group":{"name":"Peak Cycles","backend_id":"101"}},{"name":"Friday Coffee Loop","start_time":"2015-12-21T09:46:11.656Z","location":{"name":"The Admiral Cafe"},"backend_type":"facebook","backend_id":"002","attendance":"in","group":{"name":"#nbrcc"}}]}}

## Ride Detail

Details for a given ride.

* **HTTP Verb:** `GET`

* **Parameters:**
  * `backend_type` - The name of the backend the ride is stored in (from the listings call).
  * `ride_id` - The identifier of the ride in the backend system.

* **Response:**
  * `data`
    * `rides` - A list of rides.
      * `name` - The name of the ride.
      * `details` - The details/description of the ride.
      * `start_time` - The time that the ride starts.
      * `location` - The location of the start of the ride.
        * `name` - The name of the start location.
        * `address` - (optional) The street address of the start location.
        * `latitude` - (optional) The latitude of the start location.
        * `longitude` - (optional) The longitude of the start location.
        * `backend_id` - The identifier of the location in the backend system.
        * `backend_type` - The "source" of the location in the backend system, which will be "facebook" or "strava".
      * `group` - (optional) The group this ride belongs to.
        * `name` - The name of the group.
        * `backend_id` - The identifier of the group in the backend system.
        * `backend_type` - The "source" of the group in the backend system, which will be "facebook" or "strava".
        * `backend_url` - The URL to the group in the backend system.
      * `picture_url` - (optional) A URL to a picture for this ride, likely a "cover image" for Facebook based events.
      * `backend_type` - The "source" of the event in the backend system, which will be "facebook" or "strava".
      * `backend_id` - The identifier of the ride in the backend system.

* **URL pattern:** `http://liege-api/v1/rides/<backend_type>/<ride_id>`

### Example Request/Response

    $ curl -i -X GET -H "X-Liege-Auth-Token: a3a174fd-525b-45c1-b78e-0184c0ef651b" http://liege-api.herokuapp.com/v1/rides/facebook/002
    HTTP/1.1 200 OK
    Content-Type: application/json;charset=utf-8
    Content-Length: 215

    {"data":{"ride":{"name":"Friday Coffee Loop","start_time":"2015-12-21T09:46:11.656Z","location":{"name":"The Admiral Cafe"},"backend_type":"facebook","backend_id":"002","attendance":"in","group":{"name":"#nbrcc"}}}}

    $ curl -i -X GET -H "X-Liege-Auth-Token: a3a174fd-525b-45c1-b78e-0184c0ef651b" http://liege-api/v1/rides/facebook/001
    HTTP/1.1 200 OK
    Content-Type: application/json;charset=utf-8
    Content-Length: 587

    {"data":{"ride":{"name":"Tour de Eltham","start_time":"2015-12-21T09:46:11.623Z","location":{"name":"Peak Heidelberg","address":"124 Burgundy St Heidelberg","latitude":-37.755852,"longitude":145.064769},"backend_type":"facebook","details":"Wednesday morning Peak Cycles Tour de Eltham","backend_id":"001","attendance":"in","picture_url":"http://theclimbingcyclist.com/wp-content/uploads/2010/07/Eltham-loop-ride-11.07.10-006.jpg","group":{"name":"Peak Cycles","backend_type":"facebook","backend_id":"101","backend_url":"https://www.facebook.com/groups/816037948419373/"},"distance":60}}}
