# Pass The Aux

A party song request app where guests can submit their name and a song title. Requests enter a first-in, first-out queue and automatically advance to "Now Playing" every 10 seconds to simulate song playback without using real audio. 

## How To Run

All project files are located in the root directory of this repository. The test suite is built with the CS 2114 student.TestCase library. You can launch the program by running the main method inside `PassTheAuxApp.java`

**Model, No View (JUnit tests):** run these as JUnit tests in Eclipse. None of
them creates a window or a controller.

- `LinkedQueueTest`: tests the queue methods (enqueue, dequeue, peek, isEmpty, getSize, clear, contains, toArray, plus stretch enqueueFront and remove)
- `SongTest`: trimming, bad constructor input, equality ignoring case
- `RequestValidatorTest`: blank, too long, and symbols-only input
- `PartyQueueTest`: adding requests, duplicates, the 2-song limit, `tick()`, `playNext()`

**Thin view:** run `PassTheAuxApp` (it has `main`). Type a name and song title, click
Add, and watch the song move from Up Next to Now Playing after 10 seconds.

## Classes

| Role | Class | Responsibility |
|---|---|---|
| Model | `LinkedQueue<T>`, `QueueInterface<T>` | Singly linked FIFO queue with head and tail references |
| Model | `Song` | One request: title and guest name, the equals method ignores casing |
| Model | `RequestValidator` | Purely meant to check the guest's raw input  |
| Model | `RequestResult` | Display all possible outcome of a request |
| Model | `PartyQueue` | Owns the queue, Now Playing, the countdown, and the rules |
| View | `PartyWindow` | Shows the Strings and reports the Add click; imports no model class |
| Controller | `PartyController` | Handles clicks and the 1-second timer, calls the model, updates the view |
| Controller | `PassTheAuxApp` | `main` only: builds the tree structure and initializes the controller |

## Reflection

### What Changed from the Specification

| Area | Spec Said | What we Built | Why (what prompted it) |
|---|---|---|---|
| `Song` constructor | Only the title and name are trimmed | Also throws `IllegalArgumentException` on null or blank | A second line of defense, so a blank `Song` cannot be created even outside `PartyQueue` |
| `Song.hashCode` | Not mentioned | Added, matching `equals` (title, ignoring case) | Java requires `hashCode` to agree with an overridden `equals` |
| `PartyQueue.getHistory` | Listed as stretch | Built now | `playNext()` already has to move the old song somewhere, so keeping history cost one line |
| `LinkedQueue` error messages | Plain messages | First version had stray text in the messages; removed | Leftover from pasting GenAI output; caught during review for this deliverable |
| Validation | All rules in the model | Same as the spec: text rules in `RequestValidator`, queue rules in `PartyQueue.addRequest`, null and empty-queue exceptions in `LinkedQueue` | No change needed |
| Tests | Test Plan Table | Every planned case, plus `SongTest`, `testAddRequestWhilePlaying`, and `testTickClearsNowPlaying` | Reviewing the spec found the edge case where Now Playing clears; the spec's `tick()` return value was corrected to "true if Now Playing changed, including when it clears" |
| File Layout | Not specified | All classes in one folder (default package) | Matches how CS 2114 projects and Web-CAT are set up |

### Did the model really stand alone?

Yes. No model class imports Swing or refers to `PartyWindow` or `PartyController`,
and all four test classes run without a window. Nothing had to cross the
model/view line to make it run. The only formatting the view gets is
`Song.toString()` ("Levitating requested by Tuan"), which the **controller** calls
and passes on as a plain String, so the view still never sees a `Song`.

### What happened to our Scope Unknowns

| Unknown from the Scope | Status |
|---|---|
| How will we test the queue before connecting it to the GUI? | **Figured out.** JUnit tests on `LinkedQueue<String>` and `PartyQueue`, with no view |
| How do we run a 10-second timer without freezing the screen? | **Figured out.** A `javax.swing.Timer` in `PartyController` fires every second on the Swing thread; tests call `tick()` directly instead of waiting |
| How do we show the queue without dequeuing? | **Figured out.** `LinkedQueue.toArray()` copies front to back; `PartyQueue.getUpNext()` returns a copy, tested by calling it twice |
| How do we remove from the middle of a singly linked queue? | **Partly.** `LinkedQueue.remove()` relinks around the node and is tested; `PartyQueue.removeRequest` (the guest-facing part) is still open |

### What doesn't Work Yet

- `PartyQueue.removeRequest` and `addPriority` are not implemented yet (stretch).
- No JUnit tests for `PartyController` yet; the view and controller have only been checked by hand.
- A guest can dodge the 2-song limit by typing a different name (known MVP limitation).
- Guest names have no length limit; only titles are capped at 100 characters.

## Next Steps

### Left to finish the MVP, in order

1. Hand-test the full path in `PassTheAuxApp` as a group: 3 songs play 10 seconds apart, and every bad input shows its message.
2. Add a name length limit in `RequestValidator.validateRequest` (new `NAME_TOO_LONG` result) and its test.
3. Add `PartyControllerTest` for `messageFor` (every `RequestResult`, plus null).

### Change Feature: host "Play Next" (priority song)

The host can put one song at the **front** of the line/queue, for example a birthday
song. This adds model logic (front insertion that still respects duplicate and
validation rules) and a user path through the controller and view.

**Predicted to Change:**

- `PartyQueue`: new `RequestResult addPriority(String name, String title)`, which validates with `RequestValidator.validateRequest`, refuses duplicates, then calls `upNext.enqueueFront(song)`
- `PartyController`: new `handlePriority()`, registered in the constructor
- `PartyWindow`: new "Play Next" button and `addPriorityListener(ActionListener listener)`

**Predicted NOT to Change:** `LinkedQueue` and `QueueInterface` (`enqueueFront` already exists and is tested), `Song`, `RequestValidator`, `RequestResult` (existing results cover every outcome), and `PassTheAuxApp`.

### Stretch Goals

- **Committing to:** Play Next (the change feature above) and remove-my-request (`PartyQueue.removeRequest`, which uses the existing `LinkedQueue.remove`).
- **Already done:** Play history (`getHistory`) and duplicate detection.
- **Dropping:** Nothing else from the scope remains.

### New Tests Needed

- `addPriority`: valid song goes to the front (`getUpNext().get(0)`); blank or symbols-only title is refused; a duplicate is refused; queue unchanged on refusal.
- `removeRequest`: removes the guest's song and frees a slot (`countSongsBy` drops by 1); missing song returns `NOT_FOUND`; another guest's song cannot be removed.
- Name length limit: 51+ character name returns `NAME_TOO_LONG`.
- `PartyController.messageFor`: one message per `RequestResult`, generic message for null.

### Who Does What

| Item | Owner |
|---|---|
| Group Hand Test | Everyone |
| Name length limit + test | Mehad |
| `PartyControllerTest` | Toshi |
| `PartyQueue.addPriority`, `removeRequest` + tests | Toshi |
| Any `LinkedQueue` fixes that these new things need | Tuan |
| Play Next button, `addPriorityListener` | Ehren |
| `PartyController.handlePriority` | Tuan |

## Attribution

| File | Team member(s) | How they contributed |
|---|---|---|
| `QueueInterface.java` | Tuan | Created |
| `LinkedQueue.java` | Tuan | Implemented the interface and the methods required for Pass The Aux; revised by Mehad |
| `LinkedQueueTest.java` | Tuan | Created the testing class and ensured full coverage |
| `Song.java` | Tuan | Wrote and modified the appropriate methods |
| `SongTest.java` | Tuan | Tested the Song class |
| `RequestResult.java` | Toshi | Toshi wrote and finalized |
| `PartyQueue.java` | Toshi | Toshi created PartyQueue and finalized it |
| `PartyQueueTest.java` | Toshi, Tuan | Toshi made the initial tests, Tuan made sure it ran correctly |
| `RequestValidator.java` | Mehad | GenAI-assisted; reviewed and tested |
| `RequestValidatorTest.java` | Mehad | GenAI-assisted; reviewed and ran without errors |
| `PartyController.java` | Mehad | GenAI-assisted; reviewed |
| `PassTheAuxApp.java` | Ehren | GenAI-assisted; reviewed |
| `PartyWindow.java` | Ehren | GenAI-assisted thin view; reviewed |
| `README.md` | Mehad | GenAI-assisted; reviewed |

## GenAI note

We used Claude for this deliverable. Mehad asked it to check our repository against the
Deliverable 3 rubric, then to draft `RequestValidator` and its tests, and this README, following the
method signatures in our spec. It also found the stray `LinkedQueue`'s error messages, which came from earlier GenAI output. Ehren also asked GenAI to confirm that there are no errors within the code, especially when it comes to creating the GUI and the official main method. We kept our original class designs since they already matched the required specifications, verified everything with our test suite, and reviewed each file to ensure we can clearly explain the implementation.
