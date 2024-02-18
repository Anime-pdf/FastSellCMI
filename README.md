# FastSellCMI
Spigot Plugin That allows to sell items by CMI's worth rate in just one menu

### Commands:
* `/fastsell` aka `/fsell` - open menu
* `/fastsell reload` - reload configs

### Permissions:
* `fastsell.sell` - Allows `/fastsell` command. Default: all
* `fastsell.reload` - Allows `/fastsell reload` command. Default: op
* `fastsell.*` - Allows all commands. Default: op


### Config variables
#### Supports minimessages!!!

* `window-title` - Default `Remote Seller`
* `reload-msg` - Default `<green>FastSell was successfully reloaded!`
* `sell-button` - Default `<green>SELL`
* `cancel-button` - Default `<red>CANCEL`
* `price-text` - Placeholders: `<total>`, Default `<blue>Sell for: <red><total>!`
* `sell-msg` - Placeholders: `<total>`, Default `<green>Successfully sold for <red><total>!`
* `sell-sound` - Default `ENTITY_VILLAGER_CELEBRATE`
