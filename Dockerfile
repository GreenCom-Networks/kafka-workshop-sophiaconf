FROM python:3-alpine
RUN apk update && apk add bash
RUN pip install kafka-python
RUN mkdir -p /home/python
RUN mkdir -p /home/cert
ADD python/config.py /home/python/
ADD python/producer.py /home/python/
ADD python/consumer.py /home/python/
ADD python/adminClient.py /home/python/
ADD cert/ca.pem /home/cert/
ADD cert/service.cert /home/cert/
ADD cert/service.key /home/cert/
WORKDIR /home/python
RUN touch /root/.bash_history
RUN echo "python ./producer.py sophia-conf-2019.python-2" >> /root/.bash_history
RUN echo "clear && python consumer.py kafka-python-client-1 sophia-conf-2019.python-2 group-A" >> /root/.bash_history
RUN echo "clear && python consumer.py kafka-python-client-2 sophia-conf-2019.python-2 group-A" >> /root/.bash_history
RUN echo "python ./adminClient.py" >> /root/.bash_history
 