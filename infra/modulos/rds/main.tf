
resource "aws_db_subnet_group" "this" {
  name       = "${var.project_name}-subnet-group"
  subnet_ids = var.private_subnet_ids

  tags = {
    Name = "${var.project_name}-subnet-group"
  }
}

resource "aws_security_group" "this" {
  name        = "${var.project_name}-db-sg"
  description = "Controla o acesso ao banco de dados RDS"
  vpc_id      = var.vpc_id
  egress {
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }
  tags = {
    Name = "${var.project_name}-db-sg"
  }
}

resource "aws_db_instance" "this" {
  instance_class          = "db.t3.micro"
  allocated_storage       = 20
  storage_type            = "gp3"
  multi_az                = false
  backup_retention_period = 0
  skip_final_snapshot     = true
  publicly_accessible     = false
  delete_automated_backups = true

  engine         = "postgres"
  engine_version = "15"
  db_name        = var.db_name
  username       = var.db_username

  password       = var.db_password

  db_subnet_group_name   = aws_db_subnet_group.this.name
  vpc_security_group_ids = [aws_security_group.this.id]

  tags = {
    Name = "${var.project_name}-db-instance"
  }
}