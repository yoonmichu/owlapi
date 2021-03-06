/*
 * This file is part of the OWL API.
 *
 * The contents of this file are subject to the LGPL License, Version 3.0.
 *
 * Copyright (C) 2011, The University of Manchester
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see http://www.gnu.org/licenses/.
 *
 *
 * Alternatively, the contents of this file may be used under the terms of the Apache License, Version 2.0
 * in which case, the provisions of the Apache License Version 2.0 are applicable instead of those above.
 *
 * Copyright 2011, University of Manchester
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.semanticweb.owlapi.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.semanticweb.owlapi.model.OWLAnnotation;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataRange;
import org.semanticweb.owlapi.model.OWLObject;
import org.semanticweb.owlapi.model.OWLObjectVisitorEx;
import org.semanticweb.owlapi.model.OWLOntology;

/**
 * @author Matthew Horridge, The University Of Manchester, Information
 *         Management Group, Date: 29-Jul-2008
 * @param <O>
 *        the returned type
 */
public class OWLObjectWalker<O extends OWLObject> {

    protected OWLOntology ontology;
    private final Collection<O> objects;
    protected OWLObjectVisitorEx<?> visitor;
    protected final boolean visitDuplicates;
    protected OWLAxiom ax;
    protected OWLAnnotation annotation;
    private final List<OWLClassExpression> classExpressionPath = new ArrayList<OWLClassExpression>();
    private final List<OWLDataRange> dataRangePath = new ArrayList<OWLDataRange>();
    private StructureWalker<O> walker = new StructureWalker<O>(this);

    /**
     * @param objects
     *        the set of objects to visit
     */
    public OWLObjectWalker(Set<O> objects) {
        this(objects, true);
    }

    /**
     * @param visitDuplicates
     *        true if duplicates should be visited
     * @param objects
     *        the set of objects to visit
     */
    public OWLObjectWalker(Set<O> objects, boolean visitDuplicates) {
        this.objects = new ArrayList<O>(objects);
        this.visitDuplicates = visitDuplicates;
    }

    /**
     * @param walker
     *        the structure walker to use with this object walker
     */
    public void setStructureWalker(StructureWalker<O> walker) {
        this.walker = walker;
    }

    /**
     * @param v
     *        visitor to use over the objects
     */
    public void walkStructure(OWLObjectVisitorEx<?> v) {
        this.visitor = v;
        for (O o : objects) {
            o.accept(walker);
        }
    }

    /**
     * Gets the last ontology to be visited.
     * 
     * @return The last ontology to be visited
     */
    public OWLOntology getOntology() {
        return ontology;
    }

    /**
     * Gets the last axiom to be visited.
     * 
     * @return The last axiom to be visited, or {@code null} if an axiom has not
     *         be visited
     */
    public OWLAxiom getAxiom() {
        return ax;
    }

    /**
     * Gets the last annotation to be visited.
     * 
     * @return The last annotation to be visited (may be {@code null})
     */
    public OWLAnnotation getAnnotation() {
        return annotation;
    }

    /**
     * Gets the current class expression path. The current class expression path
     * is a list of class expressions that represents the containing expressions
     * for the current class expressions. The first item in the path (list) is
     * the root class expression that was visited. For i between 0 and
     * pathLength, the item at index i+1 is a direct sub-expression of the item
     * at index i. The last item in the path is the current class expression
     * being visited.
     * 
     * @return A list of class expressions that represents the path of class
     *         expressions, with the root of the class expression being the
     *         first element in the list.
     */
    public List<OWLClassExpression> getClassExpressionPath() {
        return new ArrayList<OWLClassExpression>(classExpressionPath);
    }

    /**
     * Determines if a particular class expression is the first (or root) class
     * expression in the current class expression path.
     * 
     * @param classExpression
     *        The class expression
     * @return {@code true} if the specified class expression is the first class
     *         expression in the current class expression path, otherwise
     *         {@code false} ({@code false} if the path is empty)
     */
    public boolean isFirstClassExpressionInPath(
            OWLClassExpression classExpression) {
        return !classExpressionPath.isEmpty()
                && classExpressionPath.get(0).equals(classExpression);
    }

    /**
     * Pushes a class expression onto the class expression path.
     * 
     * @param ce
     *        The class expression to be pushed onto the path
     */
    protected void pushClassExpression(OWLClassExpression ce) {
        classExpressionPath.add(ce);
    }

    /**
     * Pops a class expression from the class expression path. If the path is
     * empty then this method has no effect.
     */
    protected void popClassExpression() {
        if (!classExpressionPath.isEmpty()) {
            classExpressionPath.remove(classExpressionPath.size() - 1);
        }
    }

    /**
     * Gets the current data range path. The current data range path is a list
     * of data ranges that represents the containing expressions for the current
     * data ranges. The first item in the path (list) is the root data range
     * that was visited. For i between 0 and pathLength, the item at index i+1
     * is a direct sub-expression of the item at index i. The last item in the
     * path is the current data range being visited.
     * 
     * @return A list of data ranges that represents the path of data ranges,
     *         with the root of the data range being the first element in the
     *         list.
     */
    public List<OWLDataRange> getDataRangePath() {
        return new ArrayList<OWLDataRange>(dataRangePath);
    }

    /**
     * Pushes a data range on to the data range path.
     * 
     * @param dr
     *        The data range to be pushed onto the path
     */
    protected void pushDataRange(OWLDataRange dr) {
        dataRangePath.add(dr);
    }

    /**
     * Pops a data range from the data range expression path. If the path is
     * empty then this method has no effect.
     */
    protected void popDataRange() {
        if (!dataRangePath.isEmpty()) {
            dataRangePath.remove(dataRangePath.size() - 1);
        }
    }

    /**
     * Allow the structure walker to set the current axiom
     * 
     * @param axiom
     *        the axiom to set
     */
    public void setAxiom(OWLAxiom axiom) {
        ax = axiom;
    }

    /**
     * Allow the structure walker to set the current annotation
     * 
     * @param node
     *        the annotation to set
     */
    public void setAnnotation(OWLAnnotation node) {
        annotation = node;
    }
}
