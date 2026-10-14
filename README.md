From the repo folder, stage just that file, then commit it:
bash

git add path/to/file.java
git commit -m "Describe your change"
git push 

// Med git add gäller det att man bara commita en fil i taget. Inte hela repot. Då kan saker deleteas för andra
// Git add lägger till saker som ska commitas. Som att stapla tallrikar på en bricka
// Git commit. Skicka ut brickan till eget rum
// Git push. Skicka ut brickan till alla andra (typ restaurang)

Som princip ska vi arbeta i egna branches (grenar)

Vi skapar ny gren i github eller i terminal

git checkout -b <namn på grenen>

Exempel:
// Skapa en ny branch som heter "new branch"

git checkout -b new-branch



For example:
bash

git add src/app.js
git commit -m "Update app behavior"


To check what’s staged before committing, run git status.


Ska använda Java 25, och JUnit 6

Jag heter Adam.
