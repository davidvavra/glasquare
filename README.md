Glasquare
=========
Unofficial Foursquare glassware

Features
--------
- 3 voice commands: **Check me in**, **Find nearby places** and **Search places for ...**
- **super-fast check-ins** with a possibility to add **photos**
- **10 best venues** around you/matching your search with details
- **directions** towards a selected venue
- read **tips** of the selected venue

How to install
--------------
1. [Download latest APK](https://github.com/destil/glasquare/releases)
3. `adb install` ([read this](http://googleglassfans.com/google-glass-guides/) if you don't know what that means)

How does it look
----------------
Check [**the website**](http://destil.github.io/glasquare/)

Future plans
-----
- report bugs and feature requests as issues here
- pull requests are welcomed!

How to build the code
---------------------
1. setup your release certificate with these values in *~/.gradle/gradle.properties*: `RELEASE_STORE_FILE`, `RELEASE_STORE_PASSWORD`, `RELEASE_KEY_ALIAS`, `RELEASE_KEY_PASSWORD`
2. create a class *Hidden.java* inside package *api*, add two constants into it: `CLIENT_ID` and `CLIENT_SECRET`. Obtain these values from [Foursquare developers](https://developer.foursquare.com/).
3. go to *code* folder in terminal
4. launch `./gradlew assembleDebug` or `./gradlew assembleRelease`

* You can also import the code into Android Studio if you select top-level build.gradle file and Gradle wrapper during import.

API
----------
You can open venue detail from any GDK app like this:
```java
  try {
    Intent i = new Intent("cz.destil.glasquare.VENUE_DETAIL");
    i.putExtra("id", "41059b00f964a520850b1fe3");
    startActivity(i);
  } catch (ActivityNotFoundException e) {
    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://destil.github.io/glasquare")));
  }
```

You can also open venue detail from any Mirror API app:

```json
"menuItems" : [
    {
      "action": "OPEN_URI",
      "payload": "glasquare://venue/4b055110f964a520b75722e3"
    }
    ]
```

Author
-----
- [David 'Destil' Vávra](http://www.destil.cz)
- [Follow me on G+](http://google.com/+DavidVávra) to be notified about future versions

