variable "ssh_private_key" {}
variable "avail_zone" {}
variable "instance_type" {}
variable "home_dir" {
  default = ""
}



provider "aws" {
  region = var.avail_zone
}



data "aws_vpc" "default" {
  default = true
}

resource "aws_security_group" "lecturebot_sg" {
  name        = "lecturebot_sg"
  description = "Allow HTTP, HTTPS, and SSH"
  vpc_id      = data.aws_vpc.default.id

  ingress {
    from_port   = 22
    to_port     = 22
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"] # SSH from anywhere (secure with IP later)
  }

  ingress {
    from_port   = 80
    to_port     = 80
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }

  ingress {
    from_port   = 443
    to_port     = 443
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }

  egress {
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }

  tags = {
    Name = "lecturebot_sg"
  }

}

resource "aws_instance" "lecturebot_ec2" {
  ami           = "ami-084568db4383264d4"
  instance_type = var.instance_type
  key_name      = "vockey"

  vpc_security_group_ids = [aws_security_group.lecturebot_sg.id]
  tags = {
    Name = "lecturebot_ec2"
  }

}


resource "null_resource" "configure_server" {
  depends_on = [aws_instance.lecturebot_ec2]

  provisioner "local-exec" {
    working_dir = "${path.module}/../ansible"
    command     = "ANSIBLE_HOST_KEY_CHECKING=False ansible-playbook --inventory ${aws_instance.lecturebot_ec2.public_ip}, --private-key ${var.ssh_private_key} --user ubuntu --ssh-common-args='-o StrictHostKeyChecking=no' playbook.yml"
  }
}


output "ec2_public_ip" {
  value = aws_instance.lecturebot_ec2.public_ip
}
