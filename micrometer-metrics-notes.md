The micrometer monitoring and alerting system we're using connects to [prometheus](https://prometheus.io/docs/prometheus/latest/getting_started/) and uses [grafana](https://grafana.com/docs/guides/getting_started/) to display pretty dashboards of information.

We have two custom metrics: `endpoint_request_time` which calculates the time each endpoint execution request and `endpoint_request_time_detail` which does the same but includes the request and response bodies.



