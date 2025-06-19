def build(Map params){
    bat "docker build -f ${params.DockerfilePath} -t ${params.DockerImage} ${params.DockerArgs} ${params.DockerContext}"
}

def push(Map params){
    bat "docker push ${params.DockerImage}"
}

def promoter(Map params){
    bat "docker pull ${params.DockerImage}"
    bat "docker tag ${params.DockerImage} ${params.DockerNewImage}"
    bat "docker push ${params.DockerNewImage}"
}