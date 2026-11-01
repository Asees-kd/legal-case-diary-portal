pipeline {
    agent any

    tools {
        jdk 'JDK21'
        maven 'Maven3'
    }

    options {
        timestamps()
        timeout(time: 30, unit: 'MINUTES')
    }

    parameters {
        choice(name: 'DEPLOY_ENV', choices: ['dev', 'staging'], description: 'Deployment target environment')
        string(name: 'TOMCAT_URL', defaultValue: 'http://localhost:8080', description: 'Tomcat server URL')
    }

    triggers {
        pollSCM('H/2 * * * *')
    }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Build') {
            steps {
                bat 'mvn -B clean compile'
            }
        }

        stage('Unit Tests') {
            steps {
                bat 'mvn -B test'
            }
        }

        stage('Package') {
            steps {
                bat 'mvn -B package -DskipTests'
                archiveArtifacts artifacts: 'target/*.war', fingerprint: true
            }
        }

        stage('Selenium Tests') {
            steps {
                bat 'mvn -B test -Pselenium'
            }
        }

        stage('Deploy') {
            steps {
                echo "Deploying to ${params.DEPLOY_ENV} environment at ${params.TOMCAT_URL}..."
                withCredentials([usernamePassword(credentialsId: 'tomcat-creds', usernameVariable: 'TOMCAT_USER', passwordVariable: 'TOMCAT_PASS')]) {
                    bat '''
                        @echo off
                        echo Initiating Tomcat deployment via Manager API...
                        echo Undeploying existing application context if present...
                        curl.exe -s -S -u "%TOMCAT_USER%:%TOMCAT_PASS%" "%TOMCAT_URL%/manager/text/undeploy?path=/legal-diary" > nul 2>&1
                        echo Waiting 10 seconds for context release...
                        ping -n 11 127.0.0.1 > nul
                        echo Deploying WAR package with update=false...
                        curl.exe -s -S -u "%TOMCAT_USER%:%TOMCAT_PASS%" -T "target\\legal-diary.war" "%TOMCAT_URL%/manager/text/deploy?path=/legal-diary&update=false" > deploy_response.txt 2>&1
                        set /p RESPONSE=<deploy_response.txt
                        echo Tomcat Manager response: %RESPONSE%
                        echo %RESPONSE% | findstr /B "OK" >nul
                        if %ERRORLEVEL% NEQ 0 (
                            echo [ERROR] Deployment failed. Tomcat Manager did not return OK status.
                            type deploy_response.txt
                            del deploy_response.txt 2>nul
                            exit /b 1
                        )
                        echo [SUCCESS] Application successfully deployed to Tomcat context /legal-diary.
                        del deploy_response.txt 2>nul
                    '''
                }
            }
        }

        stage('Verify') {
            steps {
                echo "Verifying application availability in ${params.DEPLOY_ENV} environment..."
                bat '''
                    @echo off
                    echo ========================================================
                    echo DEPLOY_ENV: %DEPLOY_ENV%
                    echo Target URL: %TOMCAT_URL%/legal-diary/login
                    echo ========================================================
                    set ATTEMPT=1
                    :health_check_loop
                    echo [Attempt %ATTEMPT%/10] Probing %TOMCAT_URL%/legal-diary/login ...
                    for /f %%s in ('curl.exe -s -o nul -w "%%{http_code}" "%TOMCAT_URL%/legal-diary/login"') do set HTTP_CODE=%%s
                    echo Response HTTP Code: %HTTP_CODE%
                    if "%HTTP_CODE%"=="200" (
                        echo [SUCCESS] Verified application is live with HTTP 200 on %DEPLOY_ENV%!
                        exit /b 0
                    )
                    if %ATTEMPT% GEQ 10 (
                        echo [ERROR] Verification failed after 10 attempts on %DEPLOY_ENV%. Expected HTTP 200, got %HTTP_CODE%.
                        exit /b 1
                    )
                    set /a ATTEMPT=%ATTEMPT%+1
                    ping 127.0.0.1 -n 4 >nul
                    goto health_check_loop
                '''
            }
        }

        stage('Docker Build') {
            steps {
                echo "Building Docker images for build #${env.BUILD_NUMBER}..."
                bat 'docker build -t aseeskaurd/legal-diary:%BUILD_NUMBER% -t aseeskaurd/legal-diary:latest .'
            }
        }

        stage('Docker Push') {
            steps {
                echo "Pushing Docker images to Docker Hub..."
                withCredentials([usernamePassword(credentialsId: 'dockerhub-creds', usernameVariable: 'DH_USER', passwordVariable: 'DH_PASS')]) {
                    bat '''
                        @echo off
                        set DOCKER_CONFIG=%WORKSPACE%\\.docker
                        if not exist "%DOCKER_CONFIG%" mkdir "%DOCKER_CONFIG%"
                        echo %DH_PASS%| docker login -u %DH_USER% --password-stdin
                        if %ERRORLEVEL% NEQ 0 (
                            echo [ERROR] Docker login failed.
                            exit /b 1
                        )
                        echo Pushing tag :%BUILD_NUMBER%...
                        docker push aseeskaurd/legal-diary:%BUILD_NUMBER%
                        if %ERRORLEVEL% NEQ 0 (
                            echo [ERROR] Docker push :%BUILD_NUMBER% failed.
                            docker logout
                            exit /b 1
                        )
                        echo Pushing tag :latest...
                        docker push aseeskaurd/legal-diary:latest
                        if %ERRORLEVEL% NEQ 0 (
                            echo [ERROR] Docker push :latest failed.
                            docker logout
                            exit /b 1
                        )
                        docker logout
                    '''
                }
            }
        }

        stage('Deploy Container') {
            steps {
                echo "Deploying container for image tag aseeskaurd/legal-diary:${env.BUILD_NUMBER}..."
                bat '''
                    @echo off
                    echo Stopping and removing existing container if present...
                    docker rm -f legal-diary >nul 2>&1
                    echo Running container aseeskaurd/legal-diary:%BUILD_NUMBER% on port 8082...
                    docker run -d --name legal-diary -p 8082:8080 aseeskaurd/legal-diary:%BUILD_NUMBER%
                    if %ERRORLEVEL% NEQ 0 (
                        echo [ERROR] Failed to run docker container.
                        exit /b 1
                    )
                    echo Image Tag: aseeskaurd/legal-diary:%BUILD_NUMBER%
                    set ATTEMPT=1
                    :container_health_check
                    echo [Attempt %ATTEMPT%/20] Probing http://localhost:8082/login ...
                    for /f %%s in ('curl.exe -s -o nul -w "%%{http_code}" "http://localhost:8082/login"') do set HTTP_CODE=%%s
                    echo Response HTTP Code: %HTTP_CODE%
                    if "%HTTP_CODE%"=="200" (
                        echo [SUCCESS] Container health check passed with HTTP 200 on port 8082!
                        exit /b 0
                    )
                    if %ATTEMPT% GEQ 20 (
                        echo [ERROR] Container health check timed out after 20 attempts. Expected HTTP 200, got %HTTP_CODE%.
                        docker logs --tail 30 legal-diary
                        exit /b 1
                    )
                    set /a ATTEMPT=%ATTEMPT%+1
                    ping 127.0.0.1 -n 6 >nul
                    goto container_health_check
                '''
            }
        }
    }

    post {
        always {
            junit 'target/surefire-reports/*.xml'
            archiveArtifacts artifacts: 'target/screenshots/**', allowEmptyArchive: true
        }
    }
}
