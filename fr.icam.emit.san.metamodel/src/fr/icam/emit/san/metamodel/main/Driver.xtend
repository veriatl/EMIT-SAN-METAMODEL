/*******************************************************************************
 * Copyright (c) 2012 itemis AG (http://www.itemis.eu) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package fr.icam.emit.san.metamodel.main

import org.eclipse.emf.common.util.URI
import fr.icam.emit.san.metamodel.util.EMFUtil
import fr.icam.emit.san.metamodel.util.URIUtil
import fr.icam.emit.san.metamodel.core.SAN2Script

class Driver {
	
	def static doGeneration(URI mm_path, URI model, URI output_uri){	
		val resource_set = EMFUtil.loadEcore(mm_path)
		val resource = resource_set.getResource(model, true)
		
		var content = ""
		val compiler = new SAN2Script
		content += compiler.mapEObjects(resource.contents)	
		URIUtil.write(output_uri, content)
	}
	
	def static void main(String[] args) {
		val m_path = "./resources/San.xmi"
		val mm_path = "./resources/San.ecore"

		val m_uri = URI.createFileURI(m_path);
		val mm_uri = URI.createFileURI(mm_path)
		val output_path = "./resources/Script.txt"
		val output_uri = URI.createFileURI(output_path);
		
		
        doGeneration(mm_uri, m_uri, output_uri)

    }
}
