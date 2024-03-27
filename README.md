[Modrinth](https://modrinth.com/plugin/fastsellcmi)
|
[SpigotMC](https://www.spigotmc.org/resources/fastsellcmi.108261/)
|
[Discord](https://discord.gg/nJr7vPjzNy)

# FastSellCMI
PaperMC Plugin That allows to sell items by CMI's worth rate in just one menu 
<p align="center">
    <img src="https://github.com/Anime-pdf/FastSellCMI/assets/55580385/2b8d47b1-4490-46d3-a200-b6de92775a7f" width="450">
</p>
**Version: Paper, Folia, Purpur and other forks of Paper, 1.18.2+**

**Java: 17+**

**Supports minimessages and has configurable menu!**

<p align="center">
    <a href="https://discord.gg/nJr7vPjzNy">
        <img src="https://i.imgur.com/JgDt1Fl.png" width="300">
    </a>
    <br/>
    <i>Please join the Discord if you have questions!</i>
</p>

## Commands:
* `/fastsell`
* * Aliases: `/fsell`
* * `<no arguments>` - open sell menu
* * `reload` - reload configs

## Permissions:
* `fastsell.sell` - Allows `/fastsell` command. Default: all
* `fastsell.reload` - Allows `/fastsell reload` command. Default: op
* `fastsell.*` - Allows all commands. Default: op

## Config

### config.yml

| Variable           | Default Value                         | Placeholders                             |
|--------------------|---------------------------------------|------------------------------------------|
| config-reloaded    | `<green>FastSellCMI config reloaded!` |                                          |
| missing-permission | `<red>Not enough permissions!`        |                                          |
| sell-message       | `<green>Items sold! You got {total}`  | `{total}` - Total amount of received money |
| cancel-message     | `<red>You canceled selling!`          |                                          |
| sell-sound         | `ENTITY_VILLAGER_TRADE`               |                                          |
| cancel-sound       | `ENTITY_VILLAGER_NO`                  |                                          |

### sell_menu.yml

| Variable       | Default Value                                                                                                                                                                                                                                                                                    | Description                                                                                                                                                                                                                                                                             |
|----------------|--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| window-title   | `<green>Fast sell you items!`                                                                                                                                                                                                                                                                    |                                                                                                                                                                                                                                                                                         |
| inventory-map  | <pre>- WWWWWWWWW<br>- W       W<br>- W       W<br>- W       W<br>- W       W<br>- WSSSPCCCW</pre>                                                                                                                                                                                                | Basically an inventory layout 9x6 size.                                                                                                                                                                                                                                                 |
| item-map       | <pre>  P:<br>    end_crystal 1:<br>      name: '\<blue>Sell for: \<red>{total}'<br>  S:<br>    green_stained_glass_pane 1:<br>      name: \<green>SELL<br>  C:<br>    red_stained_glass_pane 1:<br>      name: \<red>CANCEL<br>  W:<br>    purple_stained_glass_pane 1:<br>      name: ' '</pre> | Item properties corresponding to `inventory-map` characters, supports: <br> `name`: `String` <br> `lore`: `List<String>` <br> `cmd`(custom model data): `Int` <br> `unbreakable`: `Bool` <br> `durability`: `Int` <br> `item_flags`: `List<String>` <br> `enchantments`: `List<String>` |
| functional-map | <pre>  P: price-button<br>  S: sell-button<br>  C: cancel-button<br>  W: wall-button</pre>                                                                                                                                                                                                       | Button 'types', corresponds to `inventory-map` characters.                                                                                                                                                                                                                              |
