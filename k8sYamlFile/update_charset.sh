#!/bin/bash

# MySQL root password
MYSQL_ROOT_PASSWORD="NewPassword1!"

# List of pods and corresponding databases and tables
declare -A databases
databases=(
  ["mysql-chungcheong"]="chungcheong"
  ["mysql-gangwon"]="gangwon"
  ["mysql-gyeonggi"]="gyeonggi"
  ["mysql-gyeongsang"]="gyeongsang"
  ["mysql-jeju"]="jeju"
  ["mysql-jeolla"]="jeolla"
)

# Function to update charset and collation for a given database and tables
update_charset_and_collation() {
  local pod_name=$1
  local database=$2
  local tables=("$@")

  echo "Updating character set and collation for pod $pod_name and database $database"

  for table in "${tables[@]:2}"; do
    kubectl exec -i $pod_name -- mysql -u root -p$MYSQL_ROOT_PASSWORD -e "
      USE $database;
      ALTER TABLE ${table} CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
    "
  done
}

# Iterate over each pod and update the character set and collation
for pod_label in "${!databases[@]}"; do
  pod_name=$(kubectl get pods -l app=$pod_label -o jsonpath="{.items[0].metadata.name}")
  database=${databases[$pod_label]}

  case $database in
    "chungcheong")
      update_charset_and_collation $pod_name $database "ChungcheongAirQuality" "ChungcheongStationInfo"
      ;;
    "gangwon")
      update_charset_and_collation $pod_name $database "GangwonAirQuality" "GangwonStationInfo"
      ;;
    "gyeonggi")
      update_charset_and_collation $pod_name $database "GyeonggiAirQuality" "GyeonggiStationInfo"
      ;;
    "gyeongsang")
      update_charset_and_collation $pod_name $database "GyeongsangAirQuality" "GyeongsangStationInfo"
      ;;
    "jeju")
      update_charset_and_collation $pod_name $database "JejuAirQuality" "JejuStationInfo"
      ;;
    "jeolla")
      update_charset_and_collation $pod_name $database "JeollaAirQuality" "JeollaStationInfo"
      ;;
  esac
done