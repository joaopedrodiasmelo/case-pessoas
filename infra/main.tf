
module "vpc" {
  source = "./modulos/vpc"

  project_name       = var.project_name
  availability_zones = var.availability_zones
}

module "app_secrets" {
  source = "./modulos/secrets_manager"

  secret_name   = var.app_secret_name

  secret_values = var.app_secret_values
}

module "rds" {
  source = "./modulos/rds"

  project_name       = var.project_name
  db_name            = var.db_name
  db_password        = var.db_password
  vpc_id             = module.vpc.vpc_id
  private_subnet_ids = module.vpc.private_subnet_ids

  depends_on = [module.vpc]
}

module "db_secrets" {
  source = "./modulos/secrets_manager"

  secret_name = var.db_secret_name

  secret_values = {
    "spring.datasource.url"      = "jdbc:postgresql://${module.rds.db_instance_address}:${module.rds.db_instance_port}/${module.rds.db_name}"
    "spring.datasource.username" = module.rds.db_username
    "spring.datasource.password" = module.rds.db_password
  }

  depends_on = [module.rds]
}

module "ec2" {
  source = "./modulos/ec2"

  project_name = var.project_name
  vpc_id       = module.vpc.vpc_id

  public_subnet_ids = module.vpc.public_subnet_ids

  app_secret_arn = module.app_secrets.secret_arn
  db_secret_arn  = module.db_secrets.secret_arn

  ami_id = var.ami_id

  depends_on = [
    module.vpc,
    module.app_secrets,
    module.db_secrets
  ]
}

resource "aws_security_group_rule" "app_to_db" {
  type                     = "ingress"
  description              = "Permite acesso do App (EC2) ao RDS (Postgres)"
  from_port                = 5432
  to_port                  = 5432
  protocol                 = "tcp"

  security_group_id        = module.rds.db_security_group_id

  source_security_group_id = module.ec2.app_security_group_id
}


# module "alb" {
#   source = "./modulos/alb"
#
#   project_name = var.project_name
#   vpc_id       = module.vpc.vpc_id
#
#   public_subnet_ids = module.vpc.public_subnet_ids
#
#   app_ec2_instance_id = module.ec2.instance_id
#
#   depends_on = [
#     module.vpc,
#     module.ec2
#   ]
# }
#
# resource "aws_security_group_rule" "alb_to_app" {
#   type                     = "ingress"
#   description              = "Permite acesso do ALB a EC2 na porta 8080 (Spring)"
#   from_port                = 8080
#   to_port                  = 8080
#   protocol                 = "tcp"
#
#   security_group_id        = module.ec2.app_security_group_id
#
#   source_security_group_id = module.alb.alb_security_group_id
# }
