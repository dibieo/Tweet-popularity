args<-commandArgs(trailingOnly = TRUE)

pdf(paste(args[1],".pdf",sep=""), width=6,height=3.5)

library(maps)
library(maptools)
library(RColorBrewer)
library(classInt)
library(gpclib)
library(plotrix)
library(grid)

print(paste(args[1],".txt",sep=""))
data <- read.delim(paste(args[1],".txt",sep=""), header=F, sep="\t", col.names=c("latitude", "longitude"))
map("state",col="#FFFFFF", fill=TRUE, bg="white", border="grey", lwd=0.5, mar=rep(0,4))

color=rgb(red=0, green=0, blue=255, alpha=15, max=255)
for(i in 1:length(data$longitude)){
	draw.circle(data$longitude[i], data$latitude[i],0.5,border="black",lwd=0.1,col=color,)
}

dev.off()