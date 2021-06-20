# JavaSCP
A JRE 8, pure Java, no garbage SSH file transfer client in a executable jar for restricted environments.

Supported operations:

* Delete file locally and remotely
* Transfer a single file to and from remote into the currently selected folder

Planned features:

* none

If you have any requests, hit me up!

# Building the executable jar

```
mvn clean compile assembly:single
```

jar is in the target folder.

# Running
First, create a config.json file next to the executable jar file with the content:

```
{
	"host": "192.168.2.2",
	"port": 22,
	"username": "",
	"password": ""
}
```

Put sensical values into host, port, username and password.

Start the jar file by double clicking it.

Alternatively


```
java -jar <JAR_FILE> 
```

should also work.
