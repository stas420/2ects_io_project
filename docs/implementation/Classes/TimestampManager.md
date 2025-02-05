#### Package: *timestamping*

A global, Singleton class, which provides timestamping screenshots and audio captures. It is used to synchronize all the data in output note, and sorts it.

![[timestampmanager_diagram.png]]
*Fig. 1 - Diagram picturing basically what the TimestampManager class is*

#### Methods:
- **getInstance()** $\rightarrow$ *void* - retrieves the Singleton instance;
- **Activate()** $\rightarrow$ *void*- activates the clock, i.e. sets start time and allows to fetch a timestamp;
- **getTimestamp()** $\rightarrow$ *long* - retrieves a timestamp ***in milliseconds***;
- **Deactivate()** $\rightarrow$ *void* - deactivates the time count, may be used to reset - ***warning:*** it should be closed after *every* class that uses this utility is done working, otherwise they may close themselves *not-so-gracefully*;

Exemplary usage:
```java
TimestampManager.getInstance().Activate(); // necessary for it to work

long timestamp_one = TimestampManager.getInstance().getTimestamp();
long timestamp_two = TimestampManager.getInstance().getTimestamp();

if (TimestampManager.getInstance().isActivated()) {
	TimestampManager.getInstance().Deactivate();
}

assertTrue(timestamp_one < timestamp_two);
```


