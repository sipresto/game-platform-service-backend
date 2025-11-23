## Description

Als een student het proces voor een inschrijving aan het doorlopen is 
moet hij zich kunnen inschrijvingen voor opleidingsonderdelen binnen de richting 
waarvoor hij nog geen credits heeft verworven.

## Changes to the domain model

Het concept opleidingsonderdeelInschrijving moet toegevoegd worden. 
Deze is gekoppeld aan een inschrijving van een student en aan een opleidingsonderdeel.


## Checklist
* [ ] Toon lijst waarvoor student nog geen credits verwierf
* [ ] Laat de student opleidingsonderdelen selecteren en sla deze opleidingsonderdeel
* [ ] Voeg update functionaliteit toe. 
	* Preselecteer opleidingsonderdelen waarvoor de student ingeschreven is. 
	* Verwijder inschrijvingen als deze gedeselecteerd werden.


## Acceptance criteria

```
  Background:
    Given Students
    | name | email      |  id|
    | Jan  | jan@kdg.be |  1 |
    | Piet | piet@kdg.be|  2 |

    Given Courses
    | name          | id  | credits |
    | Programmeren  | 1   | 6       |
    | Databanken    | 2   | 3       |


    Given CourseSubscriptions
    | studentId | courseId | creditReceived   |
    | 1         | 1        | TRUE             |
    | 1         | 2        | FALSE            |

    Given subscription
    | studentId | year | id |
    | 1         | 2023 | 1  |

    Scenario: Student subscribes to a course
      Given student with id 1 edits subscription with id 1
      When student adds course with id 2
      Then subscription with id 1 has a courseSubscriptions with courseId 2

    Scenario: Student subscribes to a course he already has
        Given student with id 1 edits subscription with id 1
        When student adds course with id 1
        Then subscription with id 1 has a courseSubscriptions with courseId 1
```
