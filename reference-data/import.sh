#!/usr/bin/env bash

tenant=${1:-demo_tenant}
loan_type_storage_address=http://localhost:9130/loan-types
material_type_storage_address=http://localhost:9130/material-types
identifier_type_storage_address=http://localhost:9130/identifier-types
contributor_name_type_storage_address=http://localhost:9130/contributor-name-types
contributor_type_storage_address=http://localhost:9130/contributor-types
instance_format_storage_address=http://localhost:9130/instance-formats
instance_type_storage_address=http://localhost:9130/instance-types
classification_type_storage_address=http://localhost:9130/classification-types
shelf_location_storage_address=http://localhost:9130/shelf-locations
platform_storage_address=http://localhost:9130/platforms

for f in ./material-types/*.json; do
    curl -w '\n' -X POST -D - \
         -H "Content-type: application/json" \
         -H "X-Okapi-Tenant: ${tenant}" \
         -d @$f \
         "${material_type_storage_address}"
done

for f in ./loan-types/*.json; do
    curl -w '\n' -X POST -D - \
         -H "Content-type: application/json" \
         -H "X-Okapi-Tenant: ${tenant}" \
         -d @$f \
         "${loan_type_storage_address}"
done

for f in ./shelf-locations/*.json; do
    curl -w '\n' -X POST -D - \
         -H "Content-type: application/json" \
         -H "X-Okapi-Tenant: ${tenant}" \
         -d @$f \
         "${shelf_location_storage_address}"
done

for f in ./identifier-types/*.json; do
    curl -w '\n' -X POST -D - \
         -H "Content-type: application/json" \
         -H "X-Okapi-Tenant: ${tenant}" \
         -d @$f \
         "${identifier_type_storage_address}"
done

for f in ./contributor-name-types/*.json; do
    curl -w '\n' -X POST -D - \
         -H "Content-type: application/json" \
         -H "X-Okapi-Tenant: ${tenant}" \
         -d @$f \
         "${contributor_name_type_storage_address}"
done

for f in ./contributor-types/*.json; do
    curl -w '\n' -X POST -D - \
         -H "Content-type: application/json" \
         -H "X-Okapi-Tenant: ${tenant}" \
         -d @$f \
         "${contributor_type_storage_address}"
done

for f in ./instance-types/*.json; do
    curl -w '\n' -X POST -D - \
         -H "Content-type: application/json" \
         -H "X-Okapi-Tenant: ${tenant}" \
         -d @$f \
         "${instance_type_storage_address}"
done

for f in ./instance-formats/*.json; do
    curl -w '\n' -X POST -D - \
         -H "Content-type: application/json" \
         -H "X-Okapi-Tenant: ${tenant}" \
         -d @$f \
         "${instance_format_storage_address}"
done

for f in ./classification-types/*.json; do
    curl -w '\n' -X POST -D - \
         -H "Content-type: application/json" \
         -H "X-Okapi-Tenant: ${tenant}" \
         -d @$f \
         "${classification_type_storage_address}"
done

for f in ./platforms/*.json; do
    curl -w '\n' -X POST -D - \
         -H "Content-type: application/json" \
         -H "X-Okapi-Tenant: ${tenant}" \
         -d @$f \
         "${platform_storage_address}"
done
