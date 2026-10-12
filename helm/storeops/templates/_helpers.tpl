{{/*
Return the name of the chart
*/}}
{{- define "storeops.name" -}}
{{- .Chart.Name | lower -}}
{{- end -}}

{{/*
Return the fully qualified name
*/}}
{{- define "storeops.fullname" -}}
{{- printf "%s-%s" .Release.Name .Chart.Name | lower -}}
{{- end -}}

{{/*
Return the chart version
*/}}
{{- define "storeops.chart" -}}
{{- printf "%s-%s" .Chart.Name .Chart.Version -}}
{{- end -}}
