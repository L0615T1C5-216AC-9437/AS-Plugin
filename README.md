### Description
A Auto Sync plugin to keep players up to date.

### Building a Jar

1) download src
2) run gradlew.bat
3) go to the plugin folder in cmd. (example: `cd C:\user\one\desk\pluginfolder\`)
4) type `gradlew jar` and execute
5) done, look for plugin.jar in pluginfolder\build\libs\

Note: Highly recommended to use Java 8.

### Installing

Simply place the output jar from the step above in your server's `config/mods` directory and restart the server.
List your currently installed plugins/mods by running the `mods` command.
If you want to adjust any settings, just change the settings.json

### Configuring

To modify settings, modify the settings.json file.

It should look like this
```
{
  "async":
  {
    "spacing": 5
  }
}
```
If you have more than one mod using settings.json, it should look like this
```
{
  "mod1":
  {
    "stuff": ""
  },
  "async":
  {
    "spacing": 5
  }
}
```
### Made By L0615T1C5.216AC:9437
Proud owner of Chaotic-neutral.ddns.net:1111
