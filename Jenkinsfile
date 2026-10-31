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

        stage('Deploy') {
            steps {
                echo "Deploying to ${params.DEPLOY_ENV} environment at ${params.TOMCAT_URL}..."
                withCredentials([usernamePassword(credentialsId: 'tomcat-creds', usernameVariable: 'TOMCAT_USER', passwordVariable: 'TOMCAT_PASS')]) {
                    bat '''
                        @echo off
                        echo Initiating Tomcat deployment via Manager API...
                        curl.exe -s -S -u "%TOMCAT_USER%:%TOMCAT_PASS%" -T "target\\legal-diary.war" "%TOMCAT_URL%/manager/text/deploy?path=/legal-diary&update=true" > deploy_response.txt 2>&1
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
    }
}
