    properties([buildDiscarder(logRotator(artifactDaysToKeepStr: '', artifactNumToKeepStr: '', 
    daysToKeepStr: '', numToKeepStr: "15"))])

def execCommand(command){
	println "command=" + command
	if( isUnix() ){
	    println "execute unix command"
		sh "${command}"
	}
	else{
	    println "execute windows command"
		bat "${command}"
	}
}

def call(Map pipelineParams) {



    /* pipeline {
        agent any

        stages {
            stage('docker build') {
                steps {
                    script {
                        dockerLib.build(DockerfilePath: pipelineParams.dockerfilePath,
                                        DockerImage: pipelineParams.dockerImage,
                                        DockerContext: pipelineParams.dockerContext)
                    }
                }
            }
            stage('docker push') {
                steps {
                    script {
                        dockerLib.push(DockerImage: pipelineParams.dockerImage)
                    }
                }
            }
        }
    } */
    pipeline {
	
    agent any
    
    
    // -- Display a timestamp on the log.
    options{timestamps()}
	
    stages {


        // ------------------------------------
		// -- STAGE: Initial Configuration
		// ------------------------------------
		stage("1 - clean workspace & install depencencies") {
			steps {
				
				// -- Clean Workspace
				echo "Clean Workspace"
				cleanWs()
			}
		}
		
		// Parameters needed: JOB_GIT_BRANCH, JOB_GIT_URL, GIT_CREDENTIAL
		// --------------------------------
		// -- STAGE: Download GIT Code
		// --------------------------------
		stage("2- Download GIT Code") {
			steps {
				script {
						println "git"
						checkout([$class: 'GitSCM', branches: [[name: '$JOB_GIT_BRANCH']], doGenerateSubmoduleConfigurations: false, extensions: [], submoduleCfg: [], userRemoteConfigs: [[credentialsId: "$GIT_CREDENTIAL", url: '$JOB_GIT_URL']]])
						git branch: '$JOB_GIT_BRANCH', credentialsId: "$GIT_CREDENTIAL", url: '$JOB_GIT_URL'
				}   
			}
		}

		
		// Parameters needed: JOB_IMAGE
        // ---------------------------------------
		// -- STAGE: Docker build
		// ---------------------------------------
		stage("2 - docker build") {
			steps {
				script {
					println "build"
						execCommand( "docker build -t ${JOB_IMAGE} .")
				}   
			}
		}

		// Parameters needed: JOB_CONTAINER
        // ---------------------------------------
		// -- STAGE: Docket stop & remove
		// ---------------------------------------
		stage("3 - docker stop and remove") {
		steps {
			script {
					println "stop & remove"
					try{
						execCommand( "docker stop ${JOB_CONTAINER} && docker rm ${JOB_CONTAINER}")
					}catch(Exception e){
						echo "La excepción capturada fue: ${e}"
					}
				}   
			}
		}

		// Parameters needed: JOB_CONTAINER JOB_IMAGE JOB_PORTS
        // ---------------------------------------
		// -- STAGE: Docket stop & remove
		// ---------------------------------------		
		stage("4 - run container") {
		steps {
			script {
				println "run"
					execCommand( "docker run -d --name ${JOB_CONTAINER} -p ${JOB_PORTS} ${JOB_IMAGE}")
				}   
			}
		}

	}
}


}

