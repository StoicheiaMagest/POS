Regler före Repo




Hur man använder Git:

# Hämta repo #

För att hämta repot klicka på:
    git clone https://github.com/StoicheiaMagest/POS.git

# Hämta senaste #
För att hämta senaste version gör:
    git pull

Grundläggande är att vi har versionshantering med Git. Det går att använda terminal eller IDE. Kommandon nedan är för git kommandon i terminal.

Alla ska arbeta på egen branch till att börja med. Det finns två varianter på branches. Man gör alla sina ändringar i en enstaka personlig gren, t ex <namn-gren> eller ha flera branches där varje branch hanterar ändringar i en klass.

Det viktiga är att varje gren du arbetar i har ditt namn. Se nedan för format.

Varje klass hanteras av en person i taget. Om man vill kollaborera med en persons klass då kommer man överens med den andra personen. 



# Format på gren namn #

Format:
    <namn-gren> 
    <namn-klass>
Exempel: 
    "carl-gren" eller "carl-scanner"

# Skapa en gren #

Hur du skapar en gren:
    git branch <branch-name>
Exempel:
    git branch carl-gren

Detta skapar en lokal gren som finns på din dator. Alla ändringar spåras då bara lokalt. För att skicka till github gör följande:

Skicka grenen till Github
    git push -u origin <branch-name>

Exempel:
    git push -u origin carl-gren

Origin är original repo och är "main"

# Se vilken branch du arbetar i #
I terminal kan du se vilken gren du är i genom:
    git branch --show-current
    
    R: new-branch

Visa alla lokala grenar:
    git branch 
    R:  main
    R: * new-branch

Detta visar alla lokala repo. En stjärna står framför den gren som du är i. Du vill i allmänhet arbeta i din egen gren.

# Byt gren #
Om du vill byta gren gör du:
    git switch <branch-name>
I ett exempel kan vi byta från main till new-branch och sedan tillbaka:
    git switch main
    git switch new-branch
Så länge det inte finns någon konflikt mellan grenarna så går det att byta enkelt. Om det finns en konflikt måste detta lösas innan bytet görs. 

Obs: Om man vill ha det enkelt går det att helt enkelt arbeta i en enstaka gren. Då slipper man detta. I så fall arbetar man med att pusha sina ändringar till main genom Pull Request.

# Pusha ändringar #
När vi är i rätt branch och vi har gjort ändringar i en fil så är det dags att pusha. Detta görs på följande sätt:

git add path/to/file.java
git commit -m "Describe your change"
git push 


Nu kommer filen file.java pushas i grenen man använder. För att göra merge med main branch 

För att din ändring ska gå med i main eller huvud repot öppna en

// Med git add gäller det att man bara commita en fil i taget. Inte hela repot. Då kan saker deleteas för andra
// Git add lägger till saker som ska commitas. Som att stapla tallrikar på en bricka
// Git commit. Skicka ut brickan till eget rum
// Git push. Skicka ut brickan till alla andra (typ restaurang)

Som princip ska vi arbeta i egna branches (grenar)


# Merge med main branch #

Gå till 

https://github.com/StoicheiaMagest/POS/pulls

Välj på högersida den branch du vill merga med main. Main ska vara till höger




# Programvara version #

De facto:
- Java 25
- JUnit 6



# Shortcuts:  #

<<<<<<< HEAD
    Formatera Java kod - Ctrl + alt + R 
    Om terminal är fast - ctrl + c
    Copy i terminal - Ctrl + shift + c
    Paste i terminal - Ctrl + shift + v
=======
Ska använda Java 25, och JUnit 6

Jag heter Adam.
>>>>>>> 8b2eb034cd5946a9cc60b77449faac48efe2453f
