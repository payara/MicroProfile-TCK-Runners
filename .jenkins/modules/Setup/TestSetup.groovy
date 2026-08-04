def ASADMIN = "${pwd()}/${getPayaraDirectoryName(CFG.'build.version')}/bin/asadmin"

MPLModule("Test Setup", CFG)

echo '*#*#*#*#*#*#*#*#*#*#*#*#  Setting up tests  *#*#*#*#*#*#*#*#*#*#*#*#*#*#*#'
sh "${ASADMIN} create-domain --nopassword ${CFG.domain_name}"
sh "${ASADMIN} start-domain ${CFG.domain_name}"
sh "${ASADMIN} start-database || true"