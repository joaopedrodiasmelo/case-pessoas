output "secret_arn" {
  description = "O ARN (Amazon Resource Name) do secret criado."
  value       = aws_secretsmanager_secret.this.arn
}

output "secret_id" {
  description = "O ID do secret criado."
  value       = aws_secretsmanager_secret.this.id
}