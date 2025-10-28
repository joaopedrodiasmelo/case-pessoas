variable "profile" {
  default = "default"
}

variable "aws_region" {
  description = "Região da AWS para implantar os recursos."
  type        = string
  default     = "us-east-1"
}

variable "project_name" {
  description = "Nome do projeto"
  type        = string
  default     = "case-pessoas"
}

variable "availability_zones" {
  description = "Duas zonas de disponibilidade para o deploy"
  type        = list(string)
  default     = ["us-east-1a", "us-east-1b"]
}

variable "app_secret_name" {
  description = "Nome do secret para a aplicação."
  type        = string
}

variable "app_secret_values" {
  description = "Valores a serem armazenados no Secrets Manager."
  type        = map(string)
  sensitive   = true
}

variable "db_name" {
  description = "Nome do banco de dados (ex: colaborador_db)"
  type        = string
  default     = "colaborador_db"
}

variable "db_secret_name" {
  description = "Nome do segredo no Secrets Manager para as credenciais do RDS."
  type        = string
  default     = "case-pessoas/rds-credentials2"
}

variable "db_password" {
  description = "Senha mestre do banco de dados."
  type        = string
  sensitive   = true
}

variable "ami_id" {
  description = "AMI do Amazon Linux 2023 para us-east-1"
  type        = string
  default     = "ami-07860a2d7eb515d9a"
}