# Etelie backend services

Data ingress, processing, and egress

### Development environment setup

#### 0. Clone this repository

    git clone git@github.com:etelie/server.git

#### 1. Install Homebrew and add it to your PATH

Homebrew will be used to install necessary utilitites.

    /bin/bash -c "$(curl -fsSL https://raw.githubusercontent.com/Homebrew/install/HEAD/install.sh)"
    export PATH="$PATH:/opt/homebrew/bin/"

#### 2. Install AWS CLI tools

The AWS CLI and AWS Vault allow us to connect to AWS from local consoles as well as through an SDK.

    brew install awscli aws-vault

On macOS, you may need to also install the XCode developer tools

    xcode-select --install

#### 3. Install Docker and Docker Compose

Docker is used to package the application into self-contained images which are deployed to AWS.

    sudo apt install docker docker-compose

On macOS, you will also want to install [Docker Desktop](https://www.docker.com/products/docker-desktop/) to easily get the Docker daemon running on your system.

#### 4. Configure AWS Vault

AWS Vault is a convenient utility which simplifies the use of multiple AWS account roles.

##### Environment setup

Check that aws-vault was installed properly by querying the version

    aws-vault --version

By default, aws-vault will store your credentials in a brand new keychain vault. One side effect of this is that it will prompt you for your vault password fairly frequently. There are a couple of environment variables you can set to make it a smoother process.

    export AWS_VAULT_KEYCHAIN_NAME=login  # fewer keychain password prompts
    export AWS_VAULT_PROMPT=osascript     # nice dialog prompt for entering mfa auth token (use zenity on Linux)

##### Configuring credentials

Add your IAM user access credentials to the AWS Vault keyring. If you have not already been given AWS access keys, email devops@etelie.com to request access. (`ETELIE_USERNAME` should be the same as your email address before the '@' character)

    aws-vault add ETELIE_USERNAME

Initialize your `~/.aws/config` file by invoking the provided script with your Etelie username as the single argument.

    ./scripts/aws_config_init.sh ETELIE_USERNAME

Note: You should not have an `~/.aws/credentials` file.

##### Usage

You should now be able to log in to the AWS console with each of your allowed roles using AWS Vault:

    aws-vault login PROFILE_NAME

To test you can use the AWS CLI with a specific profile:

    aws-vault exec PROFILE_NAME -- aws s3 ls

Run a credential server with AWS vault to allow local services using the AWS SDK to use your role permissions as would an EC2 instance.

    aws-vault exec PROFILE_NAME --ec2-server
    echo $AWS_VAULT  # environment variable set to your PROFILE_NAME
    ps               # aws-vault forks a separate process to run the server
    exit

##### Useful shell configurations

```sh
### aws-vault
function aws_vault_with_profile() {
    local profile="$1"
    shift
    aws-vault exec ${profile} -- aws "$@"
}

alias awsp='aws_vault_with_profile $1'
alias avl='aws-vault login'
alias avlo='open "https://signin.aws.amazon.com/oauth?Action=logout&redirect_uri=https://aws.amazon.com"'
alias ave='aws-vault exec'
alias aves='aws-vault exec --ec2-server'
alias currentvaultpid='echo $(ps -ef | grep "[a]ws-vault exec" | cut -f3 -w) $(ps -ef | grep "[a]ws-vault proxy" | cut -f3 -w)' # only works with MacOS version of `cut`
alias currentvaultkill='sudo kill -9 $(currentvaultpid)'
# alias currentvaultpid='echo $(ps -ef | grep "[a]ws-vault exec")'            # less-desirable alternative for linux
alias currentvault='echo "$(test -z $AWS_VAULT && echo "*" || echo $AWS_VAULT) $(currentvaultpid)"'
```

##### Troubleshooting

- ###### `sudo` can't find aws-vault

You may encounter the following error resulting from `sudo` not having access to `aws-vault` in the system `PATH`.

    sudo: aws-vault: command not found
    aws-vault: error: exec: Failed to start credential server: exit status 1

To fix this, edit `/etc/sudoers`, and add the path to the `aws-vault` binary to the `secure_path` variable.

- ###### `osascript` not available

Linux machines will not have access to the MacOS `osascript` utility, and the default `terminal` prompt option will not work with `aws-vault exec --server`, so you will need to install the `zenity` package and set use `--prompt=zenity` or set `AWS_VAULT_PROMPT=zenity` in your shell configuration.

#### 5. Set up Docker with AWS ECR

Log in to Docker using AWS ECR credentials. Use a profile with `PowerUserAccess`.

    aws-vault exec --ec2-server PROFILE_NAME -- \
        aws ecr get-login-password | \ 
        docker login --username=AWS --password-stdin 016089980303.dkr.ecr.us-east-1.amazonaws.com

#### 6. Install the Amazon Corretto JDK 17

We use Amazon Corretto, Amazon's JDK distribution.

###### MacOS AArch64

    curl -o /tmp/corretto17.pkg -L https://corretto.aws/downloads/latest/amazon-corretto-17-aarch64-macos-jdk.pkg \
        && open /tmp/corretto17.pkg

###### MacOS x86-64

    curl -o /tmp/corretto17.pkg -L https://corretto.aws/downloads/latest/amazon-corretto-17-x64-macos-jdk.pkg \
        && open /tmp/corretto17.pkg

You can find the downloads and installation instructions for other operating systems [here](https://docs.aws.amazon.com/corretto/latest/corretto-17-ug/downloads-list.html)

##### Set your Java home path

Add to your shell configurations:

    export JAVA_HOME=$(/usr/libexec/java_home -v 17)
    export PATH="$PATH:$JAVA_HOME/bin/"

##### Set your IntelliJ project Java SDK

Because you might have multiple installations of Java on your machine, make sure IntelliJ knows to use the Corretto 17 distribution.

`File` | `Project Structure` - You can edit the JDK for the overall project in the `Project` tab, and can edit the JDK per-module under `Modules`.

#### 7. Run the setup script

Choose a location for your etelie home directory (`~/etelie` is recommended)

The script will spin up the Docker container for your local PostgreSQL instance

#### 8. Start the local database

Start the local PostgreSQL instance along with the pgAdmin web server.

    cd ./docker/server
    docker compose up

The Dockerfile script creates the `etelie` user with password `etelie+1`. The maintenance database uses the standard `postgres` name. To database system is available within the docker network as `postgres:5432`. From the browser, its address is `localhost:5434`. 

### Deployment process

Coming soon

### Contribution guidelines

#### Repository conventions

The `master` branch is kept in a production-deployable state at all times.

#### Environment variables

Developer-specific environment configurations are left blank in the `.env.template` file, which is checked in to version control. The template is copied to `.env` when running `setup.sh`, and the developer is prompted for values to fill in the blanks. To add an environment variable, be sure to add it to `.env.template`, and if the variable should not be checked in to version control, add a line to `setup.sh`:

    set_env_value "MY_ENVIRONMENT_VARIABLE"

#### Code review

All contributions must pass code review. All changes must be tested in the QA environment before merge.

The role of the code reviewer is to ensure code quality does not diminish over time, not necessarily to ensure correctness. It is the responsibility of the developer to write correct code.

Read: [The standard of code review](https://google.github.io/eng-practices/review/reviewer/standard.html)

### Contact

zachary@etelie.com

