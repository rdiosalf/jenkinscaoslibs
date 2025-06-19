/* def build(Map params){
    echo "Building Docker image with parameters: ${params}"
    bat "docker build -f ${params.DockerfilePath} -t ${params.DockerImage} ${params.DockerArgs} ${params.DockerContext}"
} */
def build(Map params){
    echo "Building Docker image with parameters: ${params}"
    bat "docker build -f ${params.DockerfilePath} -t ${params.DockerImage} ${params.DockerContext}"
}

def push(Map params){
    echo "Pushing Docker image: ${params.DockerImage}"
    bat "docker push ${params.DockerImage}"
}

def promoter(Map params){
    bat "docker pull ${params.DockerImage}"
    bat "docker tag ${params.DockerImage} ${params.DockerNewImage}"
    bat "docker push ${params.DockerNewImage}"
}