# Live Football World Cup Score Board Java Library

![example event parameter](https://github.com/menabebawy/live-match/actions/workflows/ci-gradle.yml/badge.svg?event=push)
[![Coverage](.github/badges/jacoco.svg)](https://github.com/menabebawy/live-match/actions/workflows/ci-gradle.yml)

## Overview

The following repo contains a new Live Football World Cup Scoreboard library that shows all the ongoing matches and
their scores. It developed using **TDD**.

## Test Coverage

Run `gradle.build` then Navigate to `build > reports > tests` and open `index.html` using a web browser.

## Guidelines

To use the library all you need is just getting an instance of `LiveScoreboardApi`.

```java
LiveScoreboardApi liveScoreboardApi = LiveScoreboardApi.createInstance();
```

Then you can enjoy consuming the functionality.

## Functionally

### Start a new match

To start a new match, just please use the following call. If the match has been started successfully, you get an object
of match `Match` when you can very easily update and finish it.

If the match could not be start for any reason, a thrown exception `StartNewMatchException` can tell about the reason.
Therefore, it should be surrounded using `try catch`.

```java
Match createMatch = liveScoreboardApi.startNewMatch(String firstTeamName, String secondTeamName);
```

### Update match

Once the score of the started match has been change, the match's score can be updated by the following. For some reason
the match could not be updated, for instance the match is already finished. At this point you are supposed to get an
exception telling the reason. Therefore, it should be surrounded using `try catch`.

```java
Match createMatch = liveScoreboardApi.updateMatch(String id,
int firstTeamScore,
int secondTeamScore);
```

### Finish match

When the time is over and match just finished, then you can finish the match by calling the following. It throws
an exception when it could not finish it, for instance, you try to finish match that it had been finished. Therefore, it
should be surrounded using `try catch`.

```java
Match createMatch = liveScoreboardApi.finshMatch(String id);
```

### Scoreboard screen

It is always beneficial to keep yourself update with the current matches in progress. An api can be used to draw all in
progress matches sorted by score. The matches with the same total score will be returned ordered by the most recently
started ones. This interface returns an object which is `Scoreboard` where you can get `List<Match>` as well as `String`
that draws the summery. An empty list and empty string are supposed to be got when there is not any match in progress
currently.

```java
Scoreboard scoreboard = liveScoreboardApi.createScoreboard();
```

## Tech-stack

+ Java 17
+ Gradle
+ JUnit 5
+ Git

**_Enjoy your time with the Live Football World Cup Score_**
