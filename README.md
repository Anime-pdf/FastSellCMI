# FastSellCMI
Spigot Plugin That allows to sell items by CMI's worth rate in just one menu  

**Supports minimessages and has configurable menu!**

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
