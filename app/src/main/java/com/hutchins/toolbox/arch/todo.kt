package com.hutchins.toolbox.arch


/*
-Remove databinding.
-trim out unnecessary code.
- Known issues, this won't work if you use DialogFragments from the Nav Graph. The state machine gets desynchronized. Recommend using DialogFragments the old fashioned way. 


-Upgrade to alpha5
-Refactor back and up override to try and use navigation new features.
-Remove need for saveState and restoreState (viewModels)
-Need to allow nav structure to accept non -BaseNavFragments as navigations.
    -Also test the above.
-Go over interfaces and method names.
    -Revisit the need for supportNavUp and onBackPressed
-Expand more to the view delegate like keycode events.
-Documentation
-Unit tests

TESTING
-Test forward and back navigations
-Test constants used in nav graph as arguments
-Test overriding and that overriding persists through various screen rotations and navigations. All screens on back stack should
restore overrides, but if popped, they reset back to their initial defaults
 */