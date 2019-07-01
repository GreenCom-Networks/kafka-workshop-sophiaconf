FROM python:3-alpine



WORKDIR /kafka
COPY certs certs

WORKDIR /kafka/python
COPY src .

RUN pip install kafka-python


RUN echo "python ./producer.py sophia-conf-2019.python-2" >> /root/.bash_history
RUN echo "clear && python consumer.py kafka-python-client-1 sophia-conf-2019.python-2 group-A" >> /root/.bash_history
RUN echo "clear && python consumer.py kafka-python-client-2 sophia-conf-2019.python-2 group-A" >> /root/.bash_history
RUN echo "python ./adminClient.py" >> /root/.bash_history