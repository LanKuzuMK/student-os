# Teammate Contribution Task: Mobile Usability Review

**Assigned to:** Nhouv Vanne (`imnhouvvanne-bot`)  
**Estimated time:** 25–40 minutes  
**Contribution type:** Real device testing and documentation  
**Why it matters:** StudentOS has responsive navigation and saved light/dark appearance. A real phone review is useful evidence that the interface works outside the development computer.

## What You Will Do

Use your own phone or tablet to check the StudentOS website. Record what you actually see. Do not invent results. If something does not work, write that clearly.

## Safe Test Scope

1. Open the public StudentOS home page on your phone.
2. Check whether the text, logo, navigation, and footer are readable without rotating the device.
3. If the project owner gives you a normal test account, sign in without sharing its password in GitHub, commits, screenshots, or messages.
4. Test the mobile navigation drawer: open it, use one link, and close it.
5. Test the light/dark appearance control and confirm the setting remains after opening one other page.
6. Check the dashboard, schedule, messages, and notifications pages for obvious clipping, overlap, or unreadable text.
7. Do **not** change other users, roles, reports, account settings, passwords, or production data.

## Your Real Deliverable

Create a new file named:

```text
docs/mobile-usability-review.md
```

Use this honest template:

```md
# Mobile Usability Review

**Reviewer:** Nhouv Vanne  
**Device:** [example: Android phone / iPhone / tablet]  
**Browser:** [example: Chrome / Safari]  
**Review date:** [date]  

## Checks

| Check | Result | Notes |
| --- | --- | --- |
| Public home page readable | Pass / Issue | [What you observed] |
| Navigation drawer opens and closes | Pass / Issue | [What you observed] |
| Navigation link works | Pass / Issue | [What you observed] |
| Theme setting persists across a page change | Pass / Issue | [What you observed] |
| Dashboard is readable | Pass / Issue | [What you observed] |
| Schedule is readable | Pass / Issue | [What you observed] |
| Messages is readable | Pass / Issue | [What you observed] |
| Notifications is readable | Pass / Issue | [What you observed] |

## One Strength

[Describe one real thing that worked well.]

## One Improvement Idea

[Describe one real improvement. If you found no issue, write “No major issue observed in this review.”]
```

## GitHub Steps

1. Accept the repository invitation using your own GitHub account.
2. Create a branch named `nhouv/mobile-usability-review`.
3. Add the completed review file using the template above.
4. Commit from your own account with this message:

```text
docs: add mobile usability review
```

5. Open a pull request titled:

```text
Mobile usability review from Nhouv Vanne
```

6. In the pull-request description, state only what you actually tested and found.

## Definition of Done

The contribution is complete when the pull request has one real review file, identifies the actual device/browser used, records pass/issue results honestly, includes one observed strength and one improvement idea, and contains no credentials, private user information, screenshots of sensitive data, or destructive actions.

> This task is valuable because it contributes real mobile testing evidence to a responsive student web application. It should be committed by Nhouv Vanne from their own account after the review is actually performed.
