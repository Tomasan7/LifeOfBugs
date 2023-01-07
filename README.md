# LifeOfBugs

Tento projekt je upravená verze zadání ze školy.

## Spuštění

Spustitelný jar lze stáhnout v [GitHub releases](https://github.com/Tomasan7/LifeOfBugs/releases).

## Spuštění s kódem

1. Clonněte si tento repozitář do vybrané složky pomocí příkazu `git clone https://github.com/Tomasan7/LifeOfBugs`
2. Přejděte do složky s projektem `cd LifeOfBugs`
3. Spusťte program pomocí příkazu `.\gradlew run`

## Rozhraní

Bereme-li rozhraní ve sloupci od shore dolu:
- `Pole s broučky` - defaultně `10x10`, nebo podle načteného `map.txt` (vysvětleno níže)
- `Leaderboard` - Zobrazuje 10 broučků s největším počtem bodů
- `Ovládání` - Ovládací prvky

Najedete-li na broučka myší, zobrazí se vám jeho jméno a mozek. (algoritmus)

## Ovládání

V sekci ovládacích prvků naleznete 4 ovládací prvky:
- `Cycle` - provede jeden cyklus simulace (každý brouček provede jeden tah)
- `Restart` - nahradí a vyplní pole novými náhodnými broučky
- `Play` - opakovaně provadí cykly simulace
- `Stop` (vymění `Play`) - zastaví opakované provádění cyklů simulace, ale vždy nechá doběhnout probíhající cyklus
- `Cycle delay` - Nastavuje čas mezi tahy jednotlivých broučků (v milisekundách)

## Broučci

Každý brouček má:
- `Jméno` - Pouze pro zábavu a identifikaci broučka (zobrazí se po najetí myší na broučka)
- `Barvu` - Pouze pro identifikaci a rozlišení broučka
- `Mozek` - Algoritmus, který brouček používá pro rozhodování svých tahů (zobrazení po najetí myší na broučka)
- `Skóre` - Počet broučků, které brouček snědl. (zobrazuje se na broučkovi a v leaderboardu)

## Průběh hry

### Tahy

- Každý brouček má v každém cyklu 1 tah.
- Brouřkův mozek má jako **vstup**: Políčko před, vpravo, vlevo a za broučkem
- Políčko může být: 
  - brouček (`Tile.BugTile`)
  - zeď (`Tile.Wall`) - **toto políčko je ve hře, ale není testované a může vyvolat neočekávané chování**
  - prázdné políčko (`Tile.Space`) 
  - okraj mapy (`Tile.Void`)
- **Výsledek mozku** je
  - Krok vpřed (`Move.FORWARD`) - sežere broučka, pokud tam nějaký je; pokud je destinace nemožná, (zeď, okraj mapy) brouček zůstává na místě
  - Otočení vlevo (`Move.ROTATE_LEFT`)
  - Otočení vpravo (`Move.ROTATE_RIGHT`)

### Respawnování

Jakmile je ve hře desetinna broučků nebo méně, pole se doplní novými náhodnými broučky.

### Konec hry

Neexistuje.

## Mozky (algoritmy)

Přidat algoritmuz lze implementováním rozhraní `Brain` a přidáním této implementace do listu v `Game.kt` (řádek `267`).

Základně jsou dvě implementace: `AggresiveBrain` a `SimpleBrain`.

## Načítání a ukládání sestavení broučků

- Sestavení broučků a políček se nazývá `Mapa`
- Při spuštění hry se mapa načte z `map.txt`
- Při vypnutí hry se mapa uloží do `map.txt`

K načítání a ukládání mapy se používá `MapSerializer`.
Hotová implementace `BasicMapSerializer` mapuje jednotlivé znaky na políčka ve hře:
- `^` - brouček otočený nahoru (`Tile.BugTile`)
- `>` - brouček otočený doprava (`Tile.BugTile`)
- `<` - brouček otočený doleva (`Tile.BugTile`)
- `v` - brouček otočený dolů (`Tile.BugTile`)
- `-` (nebo jiný znak bez významu) - prázdné políčko (`Tile.Space`)
- `#` - zeď (`Tile.Wall`)

Přidat nový `MapSerializer` lze implementací rozhraní `MapSerializer` a nahrazení staré implementace v `View.kt` (řádek `40`).