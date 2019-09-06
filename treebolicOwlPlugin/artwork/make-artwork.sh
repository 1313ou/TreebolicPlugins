#!/bin/bash

source "./lib-artwork.sh"

launch="ic_launcher.svg ic_launcher_round.svg"
app="ic_launcher.svg"
logo="logo_app.svg"

icon="ic_treebolic.svg"
splash="ic_splash.svg"

make_mipmap "${launch}" 48
make_app "${app}" 512
make_res "${logo}" 48

make_res "${icon}" 48
make_res "${splash}" 144

check_dir "${dirres}"

