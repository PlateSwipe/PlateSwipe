# Milestone M2: Team Feedback

This milestone M2 provides an opportunity to give you, as a team, formal feedback on how you are performing in the project. By now, you should be building upon the foundations set in M1, achieving greater autonomy and collaboration within the team. This is meant to complement the informal, ungraded feedback from your coaches given during the weekly meetings or asynchronously on Discord, email, etc.

The feedback focuses on two major themes:
First, whether you have adopted good software engineering practices and are making progress toward delivering value to your users.
Is your design and implementation of high quality, easy to maintain, and well tested?
Second, we look at how well you are functioning as a team, how you organize yourselves, and how well you have refined your collaborative development.
An important component is also how much you have progressed, as a team, since the previous milestone.
You can find the evaluation criteria in the [M2 Deliverables](https://github.com/swent-epfl/public/blob/main/project/M2.md) document.
As mentioned in the past, the standards for M2 are elevated relative to M1, and this progression will continue into M3.

We looked at several aspects, grouped as follows:

 - Design
   - [Features](#design-features)
   - [Design Documentation](#design-documentation)
 - [Implementation and Delivery](#implementation-and-delivery)
 - Scrum
   - [Backlogs Maintenance](#scrum-backlogs-maintenance)
   - [Documentation and Ceremonies](#scrum-documentation-and-ceremonies)
   - [Continuous Delivery of Value](#scrum-continuous-delivery-of-value)

## Design: Features

We interacted with your app from a user perspective, assessing each implemented feature and flagging any issues encountered. Our evaluation focused mainly on essential features implemented during Sprints 3, 4, and 5; any additional features planned for future Sprints were not considered in this assessment unless they induced buggy behavior in the current APK.
We examined the completeness of each feature in the current version of the app, and how well it aligns with user needs and the overall project goals.


You've implemented great features that are using sensors, authentication, and use a public cloud service. They all add substantial value to the app. I couldn't give you full point here because you don't have one feature that is really innovative and show grean engineering. Don't take that as a punishment, we need a way to reward the teams that try really hard and novative features. You distinguish yourselves more by the consistency and the quality of the features you implemented and that's great.

The features you've implemented are pretty complete and polished. The UI is unified and it looks great. I couldn't give you full points here because in the "my account" screen I have a weird name when I sign in with google (a bunch of random letters, and it overlaps with itself). I also found a small bug: when spamming the "my account" icon in the navigation bar, after a few times my liked reciepe disappear from my account.

You've implemented the majority of the important features for the app to be functional and useful, in my opinion you just need the fridge. Of course there is a lot of work needed for smaller features that will glue everything together. But in my opinion you really prioritized the most important tasks/features until now, that's really good.


For this part, you received 7.5 points out of a maximum of 8.0.

## Design: Documentation

We reviewed your Figma (including wireframes and mockups) and the evolution of your overall design architecture in the three Sprints.
We assessed how you leveraged Figma to reason about the UX, ensure a good UX, and facilitate fast UI development.
We evaluated whether your Figma and architecture diagram accurately reflect the current implementation of the app and how well they align with the app's functionality and structure.


Your figma is looking really good. It's up to date with the app except for a few details: the rightmost icon in the navigation bar and the UI of the "View searched recipe screen" looks a bit different from the app.

Your architecture diagram is great, it's up to date with the app (Note that I didn't count the FilterPageViewModel.kt because it's not yet a feature implemented in the app, in my opinion, but don't forget to update the architecture when you fully add this new ViewModel). The architecture of your app is great. You've made a good system to manage the images and also to be able to grow your recipe database from OpenFoodFacts. 


For this part, you received 5.7 points out of a maximum of 6.0.

## Implementation and Delivery

We evaluated several aspects of your app's implementation, including code quality, testing, CI practices, and the functionality and quality of the APK.
We assessed whether your code is well modularized, readable, and maintainable.
We looked at the efficiency and effectiveness of your unit and end-to-end tests, and at the line coverage they achieve.


The code is polished and consistent accross the repo except a few details. It follows conventions and is well-documented (code comments). There are a few things here and there that prevented me from giving you full points here. For example in the end to end tests (End2EndTest.kt) there is a test commented out. Also there are a few unaddressed issues on main from the Sonar Cloud analysis.

The CI runs all the tests correctly, main has close to 95% coverage and all notable features have good coverage. Your end-to-end tests are a bit minimalistics though, you should now try to have end-to-end tests go through a longer workflow in through the new features you've built. For example you could do: login -> go to create a reciepe -> follow the workflow to create a recipe -> go to my account -> check that you have your recipe. This would be a really complete end-to-end test.

The APK is stable, it offers good user experience. UI looks really good. There is a bit of latency from loading the data when arriving on certain screens. For example when I go to my account I can see my recipes loading, even if minor this is a detail that can be improved for better UX. 


For this part, you received 15 points out of a maximum of 16.0.

## Scrum: Backlogs Maintenance

We looked at whether your Scrum board is up-to-date and well organized.
We evaluated your capability to organize Sprint 6 and whether you provided a clear overview of this planning on the Scrum board.
We assessed the quality of your user stories and epics: are they clearly defined, are they aligned with a user-centric view of the app, and do they suitably guide you in delivering the highest value possible.


Your Sprint Backlog is good, it's structured and tasks have a nice description, keep doing that! I think you can even improve more by being more precise with the tasks names (for example, "Delete Recipe" isn't very clear when we just look at the overview of the sprint board; you could put "Add possibility for user to delete a created recipe", or something like this), but that's really minor. You could also include the type tag of every task. You have the whole process for that described in the wiki, it's just not applied to a few tasks.

Your Product Backlog is very furnished and it's well structured. Just a few user stories that should be removed because you implemented it (for example "As a cook, I want to publish recipes, for others to be able to see while scrolling" should be in "done" because you've implemented it). Also I know you started implementing user feedback into the app. The next step would be in include user feedback into the Product Backlog (that's the job of the PO) and then use it in the sprint planning, so we ensure we are always aim to add the most value possible to the app.


For this part, you received 3.6 points out of a maximum of 4.0.

## Scrum: Documentation and Ceremonies

We assessed how you used the Scrum process to organize yourselves efficiently.
We looked at how well you documented your team Retrospective and Stand-Up during each Sprint.
We also evaluated your autonomy in using Scrum.


The team Scrum documents were all filled on time. You even included informations that weren't mentionned in the meeting. That's a good sign for the documents, but then the scrum master (or someone else) needs to mention these during the Friday meetings.

The meetings greatly improved in my opinion. You showed us that you were able to lead most of the meeting and that's good. With you the meetings always follow a good structure and you quickly integrate our feedback. The next step would be for us to not even interveen during the meeting. You direct everything and make sure they are the most valuable possible to the team.

Following on how you were able to direct the meeting, overall in the whole Scrum process, you've become a lot more independant.You manage blockers and improve on the process on your own. This is really good, we're happy with that, but don't get comfortable, you can always improve! 


For this part, you received 3.9 points out of a maximum of 4.0.

## Scrum: Continuous Delivery of Value

We evaluated the Increment you delivered at the end of each Sprint, assessing your team’s ability to continuously add value to the app.
This included an assessment of whether the way you organized the Sprints was conducive to an optimal balance between effort invested and delivery of value.


During each sprint you managed to bring value to the app, but I think you can still improve a bit more in the process of consistent delivery of value. For example there are a few PRs that couldn't be merged in one Sprint because the task was really big. In an ideal world you would be able to merge all new features/enhancements/fixes by the end of the sprint and all your work directly translates in app value that we can see on the app by the end of the week.


For this part, you received 1.8 points out of a maximum of 2.0.

## Summary

Based on the above points, your intermediate grade for this milestone M2 is 5.69. If you are interested in how this fits into the bigger grading scheme, please see the [project README](https://github.com/swent-epfl/public/blob/main/project/README.md) and the [course README](https://github.com/swent-epfl/public/blob/main/README.md).

Your coaches will be happy to discuss the above feedback in more detail.

Good luck for the next Sprints!
