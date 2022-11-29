#!groovy
echo "Grabbing Payara Artifact"
// Grab Payara artifacts from given job
copyArtifacts(projectName: CFG.buildProject, selector: specific("${CFG.buildNumber}"));
def payaraVersion = CFG.params.payaraVersion
def distributionName = "${getDistributionName()}"
def groupId = (getDistributionName().equals('micro')) ? 'fish.payara.extras' : 'fish.payara.distributions'
def fileExtension = (getDistributionName().equals('micro')) ? 'jar' : 'zip'
sh "mv ${distributionName}.${fileExtension} ${distributionName}-${payaraVersion}.${fileExtension}"
sh "mvn install:install-file -DgroupId=${groupId} -DartifactId=${distributionName} -Dversion=${payaraVersion} -Dpackaging=${fileExtension} -Dfile=${pwd()}/${distributionName}-${params.payaraVersion}.${fileExtension}"