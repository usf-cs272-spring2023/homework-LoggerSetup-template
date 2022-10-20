LoggerSetup
=================================================

![Points](../../blob/badges/points.svg)

For this homework, you will configure the `log4j2` logging library.

The **only thing** you should do for this homework assignment is import the homework and create a `log4j2` configuration file in the correct location with the correct configuration.

## Configuration

The `log4j2` third-party package should be setup automatically by `maven` when you import this homework into Eclipse.

Configure the root logger to output `INFO` messages and higher to the console and `ALL` messages to the log file. Configure the class-specific `LoggerSetup` logger to output `FATAL` messages to the console (none to the log file). The log file should be named `debug.log` and saved in the current working directory. You can use the example configuration files from lecture as a starting point.

Only output the level (unchanged), message, and **short** error message (if appropriate) to the console. The expected console output should look like:

```
Root Logger:
INFO: Ibis 
WARN: Wren 
ERROR: Eastern Eagle
FATAL: Catching Falcon

Class Logger:
FATAL: Catching Falcon
```

Include the 3 digit sequence number, level (using only 2 letters), file name, line number, thread name, message, 3 lines from any throwable stack trace (if appropriate), and a newline character (`%n`) in the `debug.log` file. See the `test/resources/debug.txt` file for the expected file output. It is also included below:

```
[001 TR] LoggerSetup.java:22 main - Turkey 
[002 DE] LoggerSetup.java:23 main - Duck 
[003 IN] LoggerSetup.java:24 main - Ibis 
[004 WA] LoggerSetup.java:25 main - Wren 
[005 ER] LoggerSetup.java:28 main - Eastern java.lang.Exception: Eagle
	at edu.usfca.cs272.LoggerSetup.outputMessages(LoggerSetup.java:28)
	at edu.usfca.cs272.LoggerSetup.main(LoggerSetup.java:39)
[006 FA] LoggerSetup.java:29 main - Catching java.lang.RuntimeException: Falcon
	at edu.usfca.cs272.LoggerSetup.outputMessages(LoggerSetup.java:29)
	at edu.usfca.cs272.LoggerSetup.main(LoggerSetup.java:39)
```

You should **NOT** modify the `LoggerSetup.java`, `LoggerSetupTest.java`, or `test/resources/debug.txt` files. You should only create a **NEW** file in the correct location to configure log4j2.

## Hints ##

Below are some hints that may help with this homework assignment:

  - For the class logger, use `edu.usfca.cs272.LoggerSetup` as the `name` attribute of the `Logger` tag in your configuration file. For example:

        ```
        <Logger name="edu.usfca.cs272.LoggerSetup" ...
        ```

  - The `log4j2.xml` file in the [lecture examples](https://github.com/usf-cs272-fall2022/lectures/) is a good starting point.

  - For configuring the output locations (where to output, file versus console), take a look at the [ConsoleAppender](https://logging.apache.org/log4j/2.x/manual/appenders.html#ConsoleAppender) and [FileAppender](https://logging.apache.org/log4j/2.x/manual/appenders.html#FileAppender) information pages.

  - For configuring the output format (what to output), I recommend you take a look at the [PatternLayout](https://logging.apache.org/log4j/2.x/manual/layouts.html#PatternLayout) information page. It includes all of the possible patterns, like `class`, `date`, `throwable` :star:, `file`, `location`, `line`, `message`, `method`, `n`, `level`, `sequenceNumber`, `threadId`, and `threadName` (you will only use some of these).

  - **Do NOT overwrite the `test/debug.log` file. You should configure log4j2 to write to the path `debug.log` instead.**

These hints are *optional*. There may be multiple approaches to solving this homework.

## Instructions ##

Use the "Tasks" view in Eclipse to find the `TODO` comments for what need to be implemented and the "Javadoc" view to see additional details.

The tests are provided in the `src/test/` directory; do not modify any of the files in that directory. Check the run details on GitHub Actions for how many points each test group is worth. 

See the [Homework Guides](https://usf-cs272-fall2022.github.io/guides/homework/) for additional details on homework requirements and submission.
