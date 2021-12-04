FROM java:8
EXPOSE 7200
ARG JAR_FILE
ADD target/${JAR_FILE} /home/app.jar
ENTRYPOINT ["java", "-jar","/home/app.jar"]

#RUN yum install -y pcre-devel wget net-tools gcc zlib zlib-devel make openssl-devel
#ADD http://nginx.org/download/nginx-1.8.0.tar.gz .
#RUN tar zxvf nginx-1.8.0.tar.gz
#RUN mkdir -p /usr/local/nginx
#RUN cd nginx-1.8.0 && ./configure --prefix=/usr/local/nginx && make && make install
#RUN rm -fv /usr/local/nginx/conf/nginx.conf
#ADD http://www.apelearn.com/study_v2/.nginx_conf /usr/local/nginx/conf/nginx.conf
#EXPOSE 80
