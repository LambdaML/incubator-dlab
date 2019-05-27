provider "google" {
  source = "../modules/global"
  project = "${var.project}"
  credentials = "${var.credentials}"
  region = "${var.region}"
}
module "service_accounts" {
  source = "../modules/global"
  service_name = "${var.project}"
}
module "vpc" {
  source = "../modules/global"
  var_ssn_public_subnet = "${var.ssn_public_subnet}"
  var_ssn_private_subnet = "${var.ssn_private_subnet}"
}
module "ssn" {
  source = "../modules/ssn"
  var_ssn_public_subnet = "${var.ssn_public_subnet}"
  var_ssn_private_subnet = "${var.ssn_private_subnet}"
}