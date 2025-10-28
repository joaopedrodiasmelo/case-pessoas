# --- 1. Permissões (IAM) ---
data "aws_iam_policy_document" "assume_role" {
  statement {
    actions = ["sts:AssumeRole"]
    principals {
      type        = "Service"
      identifiers = ["ec2.amazonaws.com"]
    }
  }
}

# Cria a Role (a "identidade" da EC2)
resource "aws_iam_role" "this" {
  name               = "${var.project_name}-ec2-role"
  assume_role_policy = data.aws_iam_policy_document.assume_role.json
}

# Anexa a política gerenciada pela AWS para o SSM (para deploy/acesso seguro)
resource "aws_iam_role_policy_attachment" "ssm" {
  role       = aws_iam_role.this.name
  policy_arn = "arn:aws:iam::aws:policy/AmazonSSMManagedInstanceCore"
}

# Anexa a política gerenciada pela AWS para ler do S3
resource "aws_iam_role_policy_attachment" "s3_read" {
  role       = aws_iam_role.this.name
  policy_arn = "arn:aws:iam::aws:policy/AmazonS3ReadOnlyAccess"
}

# Define a política customizada para ler  segredos
data "aws_iam_policy_document" "read_secrets" {
  statement {
    actions = ["secretsmanager:GetSecretValue"]
    resources = [
      var.app_secret_arn,
      var.db_secret_arn
    ]
  }
}

# Cria a política customizada
resource "aws_iam_policy" "read_secrets" {
  name   = "${var.project_name}-read-secrets-policy"
  policy = data.aws_iam_policy_document.read_secrets.json
}

# Anexa nossa política customizada à Role
resource "aws_iam_role_policy_attachment" "read_secrets" {
  role       = aws_iam_role.this.name
  policy_arn = aws_iam_policy.read_secrets.arn
}

# Cria o "Instance Profile", que conecta a Role à EC2
resource "aws_iam_instance_profile" "this" {
  name = "${var.project_name}-ec2-profile"
  role = aws_iam_role.this.name
}

# --- 2. Firewall (Security Group) ---

resource "aws_security_group" "this" {
  name        = "${var.project_name}-app-sg"
  description = "Firewall da aplicacao (EC2)"
  vpc_id      = var.vpc_id


  # ADICIONADO: Permite a App (porta 8080) da Internet
  ingress {
    from_port   = 8080
    to_port     = 8080
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"] # Permite de qualquer IP
    description = "Permite HTTP (Spring Boot) do publico"
  }

  egress {
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }

  tags = {
    Name = "${var.project_name}-app-sg"
  }
}

resource "aws_instance" "this" {
  ami           = var.ami_id
  instance_type = "t3.micro"

  subnet_id = var.public_subnet_ids[0]

  associate_public_ip_address = true

  vpc_security_group_ids = [aws_security_group.this.id]
  iam_instance_profile   = aws_iam_instance_profile.this.name

  user_data = <<-EOF
              #!/bin/bash
              yum update -y
              yum install -y java-21-amazon-corretto-devel
              EOF

  tags = {
    Name = "${var.project_name}-app-instance"
  }
}
