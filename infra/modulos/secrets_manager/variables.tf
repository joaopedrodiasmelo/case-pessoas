variable "secret_name" {
  description = "O nome do secret a ser criado no AWS Secrets Manager."
  type        = string
}

variable "secret_values" {
  description = "Um mapa (chave/valor) com os dados a serem armazenados."
  type        = map(string)
  sensitive   = true # Marca os valores como sensíveis nos logs do Terraform
}