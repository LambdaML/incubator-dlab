resource "google_service_account" "ssn_sa" {
  account_id   = "${var.service_name}-ssn-sa"
  display_name = "${var.service_name}-ssn-sa"
}

# Create a Service Account key by default
resource "google_service_account_key" "ssn_sa_key" {
  depends_on         = [google_project_iam_member.iam]
  service_account_id = google_service_account.ssn_sa.name
}

resource "google_project_iam_custom_role" "custom_ssn_role" {
  role_id     = "${var.service_name}-ssn-role"
  title       = "${var.service_name}-ssn-role"
  permissions = "${var.ssn_policy}"
}

resource "google_project_iam_member" "iam" {
  count  = "${length(var.ssn_roles)}"
  member = "serviceAccount:${google_service_account.ssn_sa.email}"
  role   = "${element(var.ssn_roles, count.index)}"
}
