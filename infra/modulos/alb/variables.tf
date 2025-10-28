variable "project_name" {
  description = "Nome do projeto, para nomear recursos."
  type        = string
}

variable "vpc_id" {
  description = "ID do VPC onde o ALB será criado."
  type        = string
}

variable "public_subnet_ids" {
  description = "Lista de IDs das sub-redes PÚBLICAS para o ALB."
  type        = list(string)
}

variable "app_ec2_instance_id" {
  description = "O ID da instância EC2 que receberá o tráfego."
  type        = string
}