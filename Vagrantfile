Vagrant.configure("2") do |config|
  config.vm.define "WMS"
  config.vm.network "forwarded_port", guest: 80, host: 8082
  config.vm.network "forwarded_port", guest: 8080, host: 8083
  config.vm.network "forwarded_port", guest: 3306, host: 3308

  config.vm.provider "virtualbox" do |vb|
    vb.memory = "2048"
    vb.cpus = 2
  end

  config.vm.provision "shell", inline: <<-SHELL
    # Update package list
    sudo apt-get update

    # Install Git
    sudo apt-get install -y git

    # Install Docker
    sudo apt-get install -y apt-transport-https ca-certificates curl gnupg lsb-release
    curl -fsSL https://download.docker.com/linux/ubuntu/gpg | sudo gpg --dearmor -o /usr/share/keyrings/docker-archive-keyring.gpg
    echo "deb [arch=amd64 signed-by=/usr/share/keyrings/docker-archive-keyring.gpg] https://download.docker.com/linux/ubuntu $(lsb_release -cs) stable" | sudo tee /etc/apt/sources.list.d/docker.list > /dev/null
    sudo apt-get update
    sudo apt-get install -y docker-ce docker-ce-cli containerd.io docker-compose-plugin

    # Add vagrant user to docker group
    sudo usermod -aG docker vagrant

    # Install Docker Compose
    sudo curl -L "https://github.com/docker/compose/releases/download/v2.24.0/docker-compose-$(uname -s)-$(uname -m)" -o /usr/local/bin/docker-compose
    sudo chmod +x /usr/local/bin/docker-compose

    # Clone the repository
    sudo -u vagrant git clone https://github.com/PlusSandBenny/WMS-WasteManagementSystem.git /home/vagrant/WMS-WasteManagementSystem
  SHELL

  config.vm.provision "shell", privileged: false, inline: <<-SHELL
    cd /home/vagrant/WMS-WasteManagementSystem
    sudo docker-compose up -d
  SHELL
end