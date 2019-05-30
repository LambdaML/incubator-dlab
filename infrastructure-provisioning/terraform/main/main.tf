provider "google" {
  version     = "~> 2.7"
  project     = "${var.project_var}"
  region      = "${var.region_var}"
  zone        = "${var.zone_var}"
}

#module "service_accounts" {
#  source = "../modules/global"
#}

module "vpc" {
  source = "../modules/global"
}

module "ssn" {
  source = "../modules/ssn"
}