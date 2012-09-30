@Grab('org.codehaus.groovy.modules.http-builder:http-builder:0.5.2')
@Grab('net.sf.opencsv:opencsv:2.3')

import groovyx.net.http.HTTPBuilder
import groovy.json.JsonSlurper
import java.util.Random
import au.com.bytecode.opencsv.CSVReader
import au.com.bytecode.opencsv.CSVParser

def metersToMilesConversion = 0.000621371
def httpBuilder = new HTTPBuilder('https://maps.googleapis.com/')

def getDistance = { origin, destination ->
	httpBuilder.get(
		path: 'maps/api/directions/json',
		query: [
			origin:      origin,
			destination: destination,
			mode:        'driving',
			units:       'imperial',
			sensor:      'false'
		]
	) { eventsResponse, eventsJson ->
		def results  = new JsonSlurper().parseText(eventsJson.toString())
		def distance = results.routes[0].legs[0].distance

		distance.value * metersToMilesConversion
	}
}

def sum = 0
args.each { file ->
	def fileSum = 0
	List<String[]> rows = new CSVReader(new FileReader(new File(file))).readAll()

	rows.each {
		def distance = getDistance(it[0], it[1])
		println "${it[0]} to ${it[1]}: ${distance} miles"

		sum     += distance
		fileSum += distance
	}
	
	println "File Total: ${fileSum}"
}

println "Grand Total: ${sum}"
