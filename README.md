
# Pass The Aux

A party song request app where guests can submit their name and a song title. Requests enter a first-in, first-out queue and automatically advance to "Now Playing" every 10 seconds to simulate song playback without using real audio. Songs that finish go into a **Played** list, and any guest can pick one and hit **Play Again** to request it again. Guests can also **Remove** their own waiting song.

## How To Compile and Run

All project files are in the root directory of this repository (default package). The tests use the CS 2114 `student.TestCase` library (`student.jar`).

### The Program

**Eclipse:** import the folder as a Java project, then right-click `PassTheAuxApp.java` → **Run As** → **Java Application**.

**Terminal** (from the repository folder; the program itself does not need `student.jar`):

```
javac LinkedQueue.java QueueInterface.java Song.java RequestResult.java RequestValidator.java PartyQueue.java PartyWindow.java PartyController.java PassTheAuxApp.java
java PassTheAuxApp
```

Using it: type a name and song title and click **Add** (or press Enter). Every 10 seconds the front of **Up Next** moves to **Now Playing**, and the finished song moves to **Played**. To replay one, click it in the **Played** list, type your name, and click **Play Again**. To take back one of your own waiting songs, click it in **Up Next**, type your name, and click **Remove**.

### The Tests

In Eclipse, add `student.jar` to the build path (Build Path → Add External Archives), then right-click the project → **Run As** → **JUnit Test** to run every test class, or right-click a single `...Test.java` file to run just that one.

The model tests create no window and no controller, so they run without the view:

- `LinkedQueueTest`: every queue method, plus order after interleaved enqueues and dequeues
- `SongTest`: trimming, bad constructor input, equality ignoring case
- `RequestValidatorTest`: blank, too long, and symbols-only input
- `PartyQueueTest`: adding requests, duplicates, the 2-song limit, `tick()`, `playNext()`, the new `playAgain()`, `removeRequest()`, and the history cap
- `PartyControllerTest`: the message shown for every `RequestResult` and for null (calls the static `messageFor`, so no window or timer is created)

`PartyWindowTest` is the one view test (it opens a window, so it needs a display).

## Classes

| Role | Class | Responsibility |
|---|---|---|
| Model | `LinkedQueue<T>`, `QueueInterface<T>` | Singly linked FIFO queue with head and tail references |
| Model | `Song` | One request: title and guest name, the equals method ignores casing |
| Model | `RequestValidator` | Purely meant to check the guest's raw input |
| Model | `RequestResult` | Every possible outcome of a request |
| Model | `PartyQueue` | Owns the queue, Now Playing, the countdown, the play history (last 20 songs), and the rules |
| View | `PartyWindow` | Shows the Strings it is given and reports clicks and which row is picked in each list; imports no model class |
| Controller | `PartyController` | Handles clicks and the 1-second timer, calls the model, updates the view |
| Controller | `PassTheAuxApp` | `main` only: builds the model, view, and controller and starts the controller |

### How the View Stays Connected without keeping Model State

Every user action and every timer tick goes through `PartyController`, which calls the model and then calls `refresh()`. `refresh()` reads the model's getters (`getNowPlaying`, `getUpNext`, `getHistory`, `getSecondsUntilNext`) and pushes plain Strings to the view. The view never holds a `Song`, a queue, a count, or a rule; its lists only contain the text it was last told to show, and that text is replaced from the model on every refresh. For Play Again and Remove the view reports only which row is selected (an `int`), and the model decides what that row means and whether the action is allowed.

## Reflection

### Switched Features: Play Next → Play Again

Our D3 Next Steps named a host **Play Next** button. For D4 we built **Play Again** instead: finished songs appear in a Played list, and a guest can pick one and request it again. Our group originally wanted guests to see the play history, and Play Next was host-only and mostly a wrapper around `enqueueFront`, which already existed and was tested. Play Again adds a new model method (`PartyQueue.playAgain`) that looks a song up in the history by position and runs it through every normal request rule, plus a new user path (the Played list and the Play Again button) through the controller and view.

### How the Design Absorbed the Change

| Class | D3 prediction (for Play Next) | What Actually Happened (Play Again) |
|---|---|---|
| `PartyQueue` | Change: new `addPriority` | **Changed:** new `playAgain(String name, int historyIndex)` |
| `PartyController` | Change: new `handlePriority` | **Changed:** new `handlePlayAgain()`, and `refresh()` now also pushes the Played list |
| `PartyWindow` | Change: new button and listener | **Changed:** Played list, Play Again button, `showHistory`, `getSelectedHistoryIndex`, `addPlayAgainListener` |
| `LinkedQueue`, `QueueInterface` | Stay unchanged | **Unchanged** |
| `Song` | Stay unchanged | **Unchanged** |
| `RequestValidator` | Stay unchanged | **Unchanged** |
| `RequestResult` | Stay unchanged | **Unchanged** (`NOT_FOUND` already covered a bad pick) |
| `PassTheAuxApp` | Stay unchanged | **Unchanged** |

Tests changed too: `PartyQueueTest` (Play Again cases) and `LinkedQueueTest` (interleaved order).

**What Matched:** exactly the three classes we predicted changed, and none of the six classes we said should stay unchanged had to change, even though we switched features. The same layers own the same jobs for both features: one new model method, one new controller handler, one new view control.

**What Surprised Us:**

- The model change was much smaller than we expected. `playAgain` is about five lines: it checks the index and then calls `addRequest`, so the blank-name, duplicate, and 2-song-limit rules came for free with no new validation code.
- The view had a bug we did not predict. The controller refreshes the screen every second, and redrawing the Played list every second wiped out the guest's selection before they could click Play Again. We fixed it inside the view only: the lists redraw only when their text actually changed. When the text does change, the pick is cleared on purpose, because the same row number may now point at a different song.
- Picking by row number creates a hidden agreement: row 0 in the Played list only means `getHistory().get(0)` because the controller builds the rows in history order. Nothing in the code enforces that besides the controller.

### Did the Change Cross the model/view line?

No. The view learned nothing new about the model. It still receives only Strings and still imports no model class; the only new thing it reports back is a row number (an `int`). The model learned nothing about the screen: `playAgain` takes a name and an index and returns a `RequestResult`. The only ripple across layers is the row-order agreement above, which lives in the controller, where it belongs.

### What changed from the D3 Plan

- **Feature:** Play Next → Play Again (explained above). `addPriority` and `handlePriority` were not built.
- **Remove Button Built with Selection.** Ehren added a Remove button and a selectable Up Next list to `PartyWindow`, so we finished the stretch goal in this deliverable: `PartyQueue.removeRequest`, `PartyController.handleRemove`, and tests. The spec's signature was `RequestResult removeRequest(String name, String title)`; we built `boolean removeRequest(String name, int upNextIndex)` instead. It takes a row number like `playAgain`, so the view never sends song text, and it returns a `boolean` so `RequestResult` did not have to change. A guest may remove only their own song.
- **History Capped at 20 Songs** (`PartyQueue.MAX_HISTORY`) so the Played list stays short at a long party.
- **`PartyControllerTest` added** for `messageFor` (every result plus null). It calls a static method, so it creates no window.
- **Signatures added:** `PartyQueue.playAgain(String, int)`, `PartyQueue.removeRequest(String, int)`, `PartyController.handlePlayAgain()`, `PartyController.handleRemove()`, and in `PartyWindow`: `showHistory(List<String>)`, `getSelectedHistoryIndex()`, `addPlayAgainListener(ActionListener)`, `getSelectedUpNextIndex()`, `addRemoveListener(ActionListener)`.
- **Validation:** no new rules. A bad pick (nothing selected, or a row outside the history) returns `NOT_FOUND`; a blank name, duplicate, or guest over the limit returns the same result `addRequest` gives. The controller shows "Pick a song from the Played list first." and "Type your name first." for those two cases, since "enter a song and name" did not fit when the song is already picked.
- **Name length limit** (D3 MVP item 2) was **not** done. It needs a new `RequestResult` value and a change to `RequestValidator`, two classes our prediction said would not change, so we moved it to the final build.
- **Tests added beyond the plan:** interleaved enqueue/dequeue order, dequeue after the queue empties, and too-long / symbols-only titles through `addRequest`.

### Bad Input: every case has a test

| Bad Input | What Happens | Test(s) |
|---|---|---|
| Blank or spaces-only name or title | Refused, message, nothing added | `RequestValidatorTest.testValidateRequestBlank`, `testSanitizeBlank`; `PartyQueueTest.testAddRequestRefused` |
| Title over 100 characters | Refused | `RequestValidatorTest.testValidateRequestTooLong`; `PartyQueueTest.testAddRequestBadTitles` |
| Symbols only, such as `@@@###` | Refused | `RequestValidatorTest.testValidateRequestSymbolsOnly`; `PartyQueueTest.testAddRequestBadTitles` |
| Song already queued or playing | Refused as duplicate | `PartyQueueTest.testAddRequestRefused`, `testAddRequestWhilePlaying` |
| Guest already has 2 songs waiting | Refused | `PartyQueueTest.testAddRequestRefused` |
| Timer fires with an empty queue | Now Playing clears, no crash | `PartyQueueTest.testTickEmpty`, `testPlayNextEmpty` |
| Removing a song that isn't there | Returns false, queue unchanged | `LinkedQueueTest.testRemove`; `PartyQueueTest.testRemoveRequestRefused` |
| **New:** Remove with a blank or null name, nothing picked, a bad row, or an empty queue | Refused, queue unchanged | `PartyQueueTest.testRemoveRequestRefused` |
| **New:** Remove another guest's song | Refused, queue unchanged | `PartyQueueTest.testRemoveRequestRefused` |
| **New:** Remove the song that is already playing | Refused (it is no longer in Up Next) | `PartyQueueTest.testRemoveRequestWhilePlaying` |
| Unknown or null result reaching the screen | Generic "Something went wrong." | `PartyControllerTest.testMessageForNull` |
| null into the queue, dequeue/peek on empty | Exception (guests never reach it) | `LinkedQueueTest.testEnqueueNull`, `testDequeueEmpty`, `testPeekEmpty`, `testDequeueAfterEmptying` |
| **New:** Play Again with nothing picked, a bad row, or empty history | `NOT_FOUND`, queue unchanged | `PartyQueueTest.testPlayAgainBadIndex` |
| **New:** Play Again with a blank or null name | `BLANK_INPUT` | `PartyQueueTest.testPlayAgainRefused` |
| **New:** Play Again on a song that is waiting or playing | `DUPLICATE` | `PartyQueueTest.testPlayAgainRefused`, `testPlayAgainWhilePlaying` |
| **New:** Play Again by a guest already at 2 songs | `GUEST_LIMIT` | `PartyQueueTest.testPlayAgainRefused` |

### What Doesn't Work yet

- Guest names have no length limit; only titles are capped at 100 characters.
- A guest can dodge the 2-song limit by typing a different name (known MVP limitation).
- `PartyController`'s click and tick handlers are checked by hand only; `PartyControllerTest` covers just the messages, because the handlers need a real window.
- If the line changes while a guest has a song picked (a song starts playing), the pick is cleared and they have to click it again.

### What would make the next Feature Cheaper

- **A private `checkRules(Song)` in `PartyQueue`.** Play Again was cheap only because it could call `addRequest` with a title. Remove-my-request already had to repeat the name check on its own, and any future feature with a different insert (Play Next, if we return to it) needs the same rules, so pulling the rule checks into one private method would let every entry point share them.
- **A small `PartyView` interface** that `PartyWindow` implements and `PartyController` talks to. Each new feature would add its methods in one place, and the controller could be tested with a fake view instead of a real Swing window.
- **Passing the picked song's text instead of a row number,** or having the controller keep the row-to-history mapping in one helper, so the row-order agreement is in one obvious place.

## Next Steps

### What is left for the final build, in order

1. **Name length limit:** a new `NAME_TOO_LONG` result in `RequestResult`, a check in `RequestValidator`, a message in `PartyController.messageFor`, and tests. We held it until after D4 is graded because it changes the two classes our D3 prediction said would stay unchanged.
2. **Group Hand Test** of the full demo path on every member's laptop, and fix anything it finds.
3. **Practice the Demo** below until it fits in 3 minutes.

### Stretch goals

- **Done:** play history (user-facing through Play Again), duplicate detection, and remove-my-request.
- **Dropping:** host Play Next / `addPriority`. Play Again replaced it as our change feature.
- **Dropping:** stopping guests from dodging the 2-song limit with a different name; fixing it would need guest accounts, which is far outside our scope.

### The 3-minute demo

| Time | What we show |
|---|---|
| 0:00 – 0:15 | Start the app; what Pass the Aux is: guests request songs, first come first served |
| 0:15 – 0:45 | Tuan adds two songs, Toshi adds one, Tuan tries a third (refused: 2-song limit) |
| 0:45 – 1:10 | Bad input: blank song, `@@@###`, and a duplicate title each show a message, no crash |
| 1:10 – 1:30 | Tuan picks his own song in Up Next and clicks Remove; then tries to remove Toshi's (refused) |
| 1:30 – 2:10 | **New feature:** pick a song in Played, type a name, Play Again; it joins the back of Up Next. Then Play Again with nothing picked (refused) |
| 2:10 – 2:45 | Run the JUnit tests in Eclipse: all green, and the model tests open no window |
| 2:45 – 3:00 | One line on how the change stayed in the three classes we predicted |

A song moves every 10 seconds from the moment the app starts, so by 1:30 at least one song has finished and is in the Played list.

### Who does what

| Item | Owner |
|---|---|
| Name length limit: `RequestValidator` check + tests | Mehad |
| `NAME_TOO_LONG` in `RequestResult` and its message | Toshi |
| Hand test on every laptop | Everyone |
| Demo: driving the app | Tuan |
| Demo: running the tests | Ehren |
| Demo: the opening and closing lines | Toshi |
| Demo: bad input and Remove | Mehad |

## Attribution

| File | Team member(s) | How they contributed |
|---|---|---|
| `QueueInterface.java` | Tuan | Created |
| `LinkedQueue.java` | Tuan | Implemented the interface and the methods required for Pass The Aux; revised by Mehad |
| `LinkedQueueTest.java` | Tuan, Mehad | Tuan created the testing class and ensured full coverage; Mehad added the interleaved-order and dequeue-after-emptying tests (GenAI-assisted) |
| `Song.java` | Tuan | Wrote and modified the appropriate methods |
| `SongTest.java` | Tuan | Tested the Song class |
| `RequestResult.java` | Toshi | Wrote and finalized |
| `PartyQueue.java` | Toshi, Mehad | Toshi created PartyQueue and finalized it; Mehad added `playAgain`, `removeRequest`, and the history cap (GenAI-assisted) |
| `PartyQueueTest.java` | Toshi, Tuan, Mehad | Toshi made the initial tests, Tuan made sure they ran correctly; Mehad added the Play Again, Remove, history-cap, and bad-title tests (GenAI-assisted) |
| `RequestValidator.java` | Mehad | GenAI-assisted; reviewed and tested |
| `RequestValidatorTest.java` | Mehad | GenAI-assisted; reviewed and ran without errors |
| `PartyController.java` | Mehad | GenAI-assisted; reviewed; added `handlePlayAgain`, `handleRemove`, and the Played list refresh |
| `PartyControllerTest.java` | Mehad | GenAI-assisted; reviewed and ran |
| `PassTheAuxApp.java` | Ehren | GenAI-assisted; reviewed |
| `PartyWindow.java` | Ehren, Mehad | Ehren: thin view (GenAI-assisted, reviewed), then added the Remove button and the selectable Up Next list; Mehad added the Played list and Play Again button and the keep-the-pick fix (GenAI-assisted) |
| `PartyWindowTest.java` | Ehren | Wrote and expanded to cover every view method (12 tests) |
| `README.md` | Mehad, Tuan | Mehad drafted (GenAI-assisted); Tuan revised |

## GenAI note

We used Claude for this deliverable. Mehad gave it our D4 rubric and our D3 Next Steps and asked what would meet every Exceeds box. It pointed out that just showing the history we already stored would count as a "button wired to logic you already have," so we chose Play Again, which adds model logic. It also suggested keeping our changes out of `RequestResult` and `RequestValidator` so our D3 prediction would hold, which is why `removeRequest` returns a `boolean`. Mehad then had it draft `PartyQueue.playAgain`, `removeRequest`, and the history cap, the Played list and Play Again button in `PartyWindow`, `handlePlayAgain` and `handleRemove` in `PartyController`, the new tests in `PartyQueueTest`, `LinkedQueueTest`, and `PartyControllerTest`, and this README. Ehren added the Remove button and the selectable Up Next list to `PartyWindow` and expanded `PartyWindowTest` himself. While checking the running app, Claude found that the once-a-second refresh was wiping out a guest's pick in both lists, and the fix stayed inside the view. We kept the code because it matched our design and every test passed, and we reviewed the new methods so each owner can explain them.
