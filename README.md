# bentoboxluckpermscontexts-plugin
Minecraft java Paper plugin to provide bentobox luckperms contexts

it autocompletes possible flags and gamemodes in luckperms commands and the web ui

provides luckperms contexts for server admins, based on bentobox gamemodes and flags:
# flag contexts

bentoboxflag:<flag_id> = allow
bentoboxflag:<flag_id> = deny
bentoboxflag:<flag_id> = unset

# gamemode contexts

bentoboxgamemode = <gamemodename>
bentoboxgamemode = none
the flag id, and gamemode name are both lowercased

# custom flags

in the config file (only loaded on start, restart the server to update config, also modification to the comments are lost)
custom flags can be defined, for the gamemodes you specify, allowing players to set custom protection levels based on their ranks.
the flag ids you put in the config are prefixed with LPCONTEXT_ to avoid conflicts

permissions: none
commands: none
